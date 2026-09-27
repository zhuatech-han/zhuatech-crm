/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.dto.CrmDto.*;
import cn.zhuatech.crm.model.Customer;
import cn.zhuatech.crm.repository.CustomerRepository;
import cn.zhuatech.crm.service.CrmAccessService;
import cn.zhuatech.crm.service.CustomerTransferService;
import cn.zhuatech.crm.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 提供受数据权限约束的客户查询、维护与负责人移交接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController @RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository customers; private final CrmAccessService access; private final CustomerTransferService transfers; private final AuditService audit;

    public CustomerController(CustomerRepository customers,CrmAccessService access,CustomerTransferService transfers,AuditService audit){this.customers=customers;this.access=access;this.transfers=transfers;this.audit=audit;}
    /**
     * 列出符合访问范围及筛选条件的业务记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @GetMapping public ApiResponse<List<CustomerView>> list(@RequestParam(defaultValue="") String keyword){
        var user=access.current(); var items=access.canViewAll(user)?customers.findAllByOrderByUpdatedAtDesc():customers.findByOwnerOrderByUpdatedAtDesc(user); String q=keyword.trim().toLowerCase(Locale.ROOT);
        return ApiResponse.ok(items.stream().filter(c->q.isEmpty()||(c.getName()+Objects.toString(c.getShortName(),"")+Objects.toString(c.getIndustry(),"")).toLowerCase(Locale.ROOT).contains(q)).map(CustomerView::from).toList());
    }
    /**
     * 读取指定客户详情并校验其访问权限。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @GetMapping("/{id}") public ApiResponse<CustomerView> detail(@PathVariable Long id){return ApiResponse.ok(CustomerView.from(access.customer(id)));}
    /**
     * 校验业务请求并持久化新增记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping @Transactional public ApiResponse<CustomerView> create(@Valid @RequestBody CustomerRequest req){Customer c=new Customer(req.name(),access.current());apply(c,req);customers.save(c);audit.record("CUSTOMER_CREATE","CUSTOMER",c.getId(),null);return ApiResponse.ok("客户已创建",CustomerView.from(c));}
    /**
     * 校验请求后更新业务记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PutMapping("/{id}") @Transactional public ApiResponse<CustomerView> update(@PathVariable Long id,@Valid @RequestBody CustomerRequest req){Customer c=access.customer(id);apply(c,req);audit.record("CUSTOMER_UPDATE","CUSTOMER",id,null);return ApiResponse.ok("客户已更新",CustomerView.from(c));}
    /**
     * 经理或管理员转移客户和关联业务归属。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PatchMapping("/{id}/owner")
    public ApiResponse<CustomerView> transfer(@PathVariable Long id, @Valid @RequestBody CustomerTransferRequest request) {
        return ApiResponse.ok("客户负责人已调整", transfers.transfer(id, request.ownerId(), request.reason()));
    }
    /**
     * 把已校验的客户请求字段写入客户实体。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    private void apply(Customer c,CustomerRequest r){c.update(r.name(),r.shortName(),r.industry(),r.level()==null?"B":r.level(),r.status(),r.source(),r.phone(),r.email(),r.address(),r.nextFollowUpDate(),r.notes());}
}
