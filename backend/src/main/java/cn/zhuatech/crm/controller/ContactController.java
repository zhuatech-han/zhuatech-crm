/* Copyright 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd. · https://www.zhuatech.cn/ */
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
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/contacts")
public class ContactController {
    private final ContactRepository contacts; private final CrmAccessService access; private final AuditService audit;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ContactController(ContactRepository contacts,CrmAccessService access,AuditService audit){this.contacts=contacts;this.access=access;this.audit=audit;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping public ApiResponse<List<ContactView>> list(@RequestParam Long customerId){return ApiResponse.ok(contacts.findByCustomerOrderByPrimaryContactDescCreatedAtAsc(access.customer(customerId)).stream().map(ContactView::from).toList());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping @Transactional public ApiResponse<ContactView> create(@Valid @RequestBody ContactRequest r){var c=access.customer(r.customerId());var item=new Contact(c,r.name(),r.title(),r.phone(),r.email(),r.primaryContact(),r.notes());contacts.save(item);audit.record("CONTACT_CREATE","CONTACT",item.getId(),null);return ApiResponse.ok("联系人已添加",ContactView.from(item));}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @DeleteMapping("/{id}") @Transactional public ApiResponse<Void> delete(@PathVariable Long id){var item=contacts.findById(id).orElseThrow(()->new BusinessException("联系人不存在"));access.customer(item.getCustomer().getId());contacts.delete(item);audit.record("CONTACT_DELETE","CONTACT",id,null);return ApiResponse.ok("联系人已删除",null);}
}
