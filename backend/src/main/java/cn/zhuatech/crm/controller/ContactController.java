/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.*;
import cn.zhuatech.crm.dto.CrmDto.*;
import cn.zhuatech.crm.model.Contact;
import cn.zhuatech.crm.repository.ContactRepository;
import cn.zhuatech.crm.service.CrmAccessService;
import cn.zhuatech.crm.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 提供客户联系人查询、创建和删除接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController @RequestMapping("/api/contacts")
public class ContactController {
    private final ContactRepository contacts; private final CrmAccessService access; private final AuditService audit;

    public ContactController(ContactRepository contacts,CrmAccessService access,AuditService audit){this.contacts=contacts;this.access=access;this.audit=audit;}
    /**
     * 列出符合访问范围及筛选条件的业务记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @GetMapping public ApiResponse<List<ContactView>> list(@RequestParam Long customerId){return ApiResponse.ok(contacts.findByCustomerOrderByPrimaryContactDescCreatedAtAsc(access.customer(customerId)).stream().map(ContactView::from).toList());}
    /**
     * 校验业务请求并持久化新增记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping @Transactional public ApiResponse<ContactView> create(@Valid @RequestBody ContactRequest r){var c=access.customer(r.customerId());var item=new Contact(c,r.name(),r.title(),r.phone(),r.email(),r.primaryContact(),r.notes());contacts.save(item);audit.record("CONTACT_CREATE","CONTACT",item.getId(),null);return ApiResponse.ok("联系人已添加",ContactView.from(item));}
    /**
     * 校验权限与关联约束后删除业务记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @DeleteMapping("/{id}") @Transactional public ApiResponse<Void> delete(@PathVariable Long id){var item=contacts.findById(id).orElseThrow(()->new BusinessException("联系人不存在"));access.customer(item.getCustomer().getId());contacts.delete(item);audit.record("CONTACT_DELETE","CONTACT",id,null);return ApiResponse.ok("联系人已删除",null);}
}
