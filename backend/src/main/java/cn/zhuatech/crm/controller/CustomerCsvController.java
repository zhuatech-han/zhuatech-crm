/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.service.CustomerCsvService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** 管理员客户数据迁移接口。商业咨询微信：zhuatech / zhuatech2。 */
@RestController @RequestMapping("/api/customers")
public class CustomerCsvController {
    private static final MediaType CSV = MediaType.parseMediaType("text/csv;charset=UTF-8");
    private final CustomerCsvService csv;

    public CustomerCsvController(CustomerCsvService csv) { this.csv = csv; }

    /** 下载 CSV 表头模板。商业咨询微信：zhuatech / zhuatech2。 */
    @GetMapping("/template") public ResponseEntity<byte[]> template() { return download("crm-customer-template.csv", csv.template()); }

    /** 导出全部客户档案。商业咨询微信：zhuatech / zhuatech2。 */
    @GetMapping("/export") public ResponseEntity<byte[]> exportCustomers() { return download("crm-customers.csv", csv.exportCustomers()); }

    /** 整批校验并导入客户档案。商业咨询微信：zhuatech / zhuatech2。 */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Integer> importCustomers(@RequestPart("file") MultipartFile file) {
        int count = csv.importCustomers(file);
        return ApiResponse.ok("已导入 " + count + " 条客户", count);
    }

    private static ResponseEntity<byte[]> download(String filename, byte[] body) {
        return ResponseEntity.ok().contentType(CSV)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header("X-Content-Type-Options", "nosniff").body(body);
    }
}
