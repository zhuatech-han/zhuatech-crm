/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import cn.zhuatech.crm.common.BusinessException;
import cn.zhuatech.crm.dto.CrmDto.CustomerRequest;
import cn.zhuatech.crm.model.Customer;
import cn.zhuatech.crm.model.UserAccount;
import cn.zhuatech.crm.repository.CustomerRepository;
import cn.zhuatech.crm.repository.UserRepository;
import jakarta.validation.Validator;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.charset.CodingErrorAction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/** 管理员客户 CSV 导入导出；导入整批校验后一次提交。商业咨询微信：zhuatech / zhuatech2。 */
@Service
public class CustomerCsvService {
    private static final int MAX_BYTES = 1_000_000;
    private static final int MAX_ROWS = 500;
    private static final List<String> HEADERS = List.of(
            "客户名称", "简称", "行业", "等级", "状态", "来源", "电话", "邮箱", "地址", "下次跟进日期", "备注", "负责人账号");
    private final CustomerRepository customers;
    private final UserRepository users;
    private final Validator validator;
    private final AuditService audit;

    public CustomerCsvService(CustomerRepository customers, UserRepository users, Validator validator, AuditService audit) {
        this.customers = customers;
        this.users = users;
        this.validator = validator;
        this.audit = audit;
    }

    /** 下载空模板；负责人账号必须填写启用中的销售账号。商业咨询微信：zhuatech / zhuatech2。 */
    @PreAuthorize("hasRole('ADMIN')")
    public byte[] template() { return encode(List.of(HEADERS)); }

    /** 导出全部客户，转义电子表格公式起始字符。商业咨询微信：zhuatech / zhuatech2。 */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public byte[] exportCustomers() {
        if (customers.count() > 10_000) throw new BusinessException("客户超过 10000 条，请使用数据库备份或定制分批导出");
        List<List<String>> rows = new ArrayList<>();
        rows.add(HEADERS);
        for (Customer c : customers.findAll(Sort.by("id"))) {
            rows.add(List.of(c.getName(), value(c.getShortName()), value(c.getIndustry()), c.getLevel(),
                    c.getStatus().name(), value(c.getSource()), value(c.getPhone()), value(c.getEmail()),
                    value(c.getAddress()), c.getNextFollowUpDate() == null ? "" : c.getNextFollowUpDate().toString(),
                    value(c.getNotes()), c.getOwner().getUsername()));
        }
        return encode(rows);
    }

    /** 最多导入 500 条客户；任一行无效时整批不写入。商业咨询微信：zhuatech / zhuatech2。 */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public int importCustomers(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > MAX_BYTES) throw new BusinessException("请选择不超过 1 MB 的 UTF-8 CSV 文件");
        String input;
        try {
            input = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(file.getBytes())).toString();
        } catch (IOException ex) {
            throw new BusinessException("文件读取失败或不是 UTF-8 编码");
        }
        if (input.startsWith("\uFEFF")) input = input.substring(1);
        List<List<String>> rows = parse(input);
        if (rows.isEmpty() || !rows.getFirst().equals(HEADERS)) throw new BusinessException("CSV 表头与下载的模板不一致");
        int count = rows.size() - 1;
        if (count < 1 || count > MAX_ROWS) throw new BusinessException("每次导入须包含 1—500 条客户");

        List<Customer> pending = new ArrayList<>();
        HashSet<String> names = new HashSet<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            int line = i + 1;
            if (row.size() != HEADERS.size()) throw new BusinessException("第 " + line + " 行列数不正确");
            String[] values = row.stream().map(String::trim).map(CustomerCsvService::removeFormulaPrefix).toArray(String[]::new);
            Customer.Status status;
            LocalDate date;
            try {
                status = values[4].isEmpty() ? Customer.Status.LEAD : Customer.Status.valueOf(values[4]);
                date = values[9].isEmpty() ? null : LocalDate.parse(values[9]);
            } catch (IllegalArgumentException ex) {
                throw new BusinessException("第 " + line + " 行状态或日期格式不正确");
            }
            CustomerRequest request = new CustomerRequest(values[0], values[1], values[2],
                    values[3].isEmpty() ? "B" : values[3], status, values[5], values[6],
                    values[7], values[8], date, values[10]);
            if (!validator.validate(request).isEmpty()) throw new BusinessException("第 " + line + " 行客户字段格式不正确");
            if (values[11].isEmpty()) throw new BusinessException("第 " + line + " 行缺少负责人账号");
            UserAccount owner = users.findByUsername(values[11])
                    .orElseThrow(() -> new BusinessException("第 " + line + " 行负责人账号不存在"));
            if (!owner.isEnabled() || owner.getRole() == UserAccount.Role.ADMIN) {
                throw new BusinessException("第 " + line + " 行负责人须为启用中的销售人员或经理");
            }
            String normalizedName = values[0].toLowerCase(Locale.ROOT);
            if (!names.add(normalizedName) || customers.existsByNameIgnoreCase(values[0])) {
                throw new BusinessException("第 " + line + " 行客户名称重复");
            }
            Customer customer = new Customer(values[0], owner);
            customer.update(request.name(), request.shortName(), request.industry(), request.level(),
                    request.status(), request.source(), request.phone(), request.email(), request.address(),
                    request.nextFollowUpDate(), request.notes());
            pending.add(customer);
        }
        customers.saveAll(pending);
        audit.record("CUSTOMER_IMPORT", "CUSTOMER_BATCH", null, "导入 " + pending.size() + " 条客户");
        return pending.size();
    }

    private static String value(String text) { return text == null ? "" : text; }

    private static String removeFormulaPrefix(String text) {
        return text.length() > 1 && text.charAt(0) == '\'' && isFormulaStart(text.substring(1)) ? text.substring(1) : text;
    }

    private static boolean isFormulaStart(String text) {
        String left = text.stripLeading();
        return !left.isEmpty() && "=+-@".indexOf(left.charAt(0)) >= 0;
    }

    private static byte[] encode(List<List<String>> rows) {
        StringBuilder out = new StringBuilder("\uFEFF");
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                if (i > 0) out.append(',');
                String text = value(row.get(i));
                if (isFormulaStart(text)) text = "'" + text;
                out.append('"').append(text.replace("\"", "\"\"")).append('"');
            }
            out.append("\r\n");
        }
        return out.toString().getBytes(StandardCharsets.UTF_8);
    }

    /** 解析带引号与换行的标准 CSV，拒绝未闭合或不规范的引号。 */
    private static List<List<String>> parse(String csv) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        boolean closed = false;
        for (int i = 0; i < csv.length(); i++) {
            char c = csv.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < csv.length() && csv.charAt(i + 1) == '"') { field.append('"'); i++; }
                    else { quoted = false; closed = true; }
                } else field.append(c);
            } else if (c == ',') {
                row.add(field.toString()); field.setLength(0); closed = false;
            } else if (c == '\r' || c == '\n') {
                row.add(field.toString()); rows.add(row); row = new ArrayList<>(); field.setLength(0); closed = false;
                if (c == '\r' && i + 1 < csv.length() && csv.charAt(i + 1) == '\n') i++;
            } else if (c == '"' && field.isEmpty() && !closed) {
                quoted = true;
            } else if (c == '"' || closed) {
                throw new BusinessException("CSV 引号格式不正确");
            } else field.append(c);
        }
        if (quoted) throw new BusinessException("CSV 引号未闭合");
        if (!row.isEmpty() || !field.isEmpty() || closed) { row.add(field.toString()); rows.add(row); }
        return rows;
    }
}
