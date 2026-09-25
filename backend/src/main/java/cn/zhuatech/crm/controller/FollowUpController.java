/* Copyright 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd. · https://www.zhuatech.cn/ */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.*;
import cn.zhuatech.crm.dto.CrmDto.*;
import cn.zhuatech.crm.model.*;
import cn.zhuatech.crm.repository.*;
import cn.zhuatech.crm.service.CrmAccessService;
import cn.zhuatech.crm.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/follow-ups")
public class FollowUpController {
    private final FollowUpRepository followUps; private final OpportunityRepository opportunities; private final CustomerRepository customers; private final CrmAccessService access; private final AuditService audit;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public FollowUpController(FollowUpRepository followUps,OpportunityRepository opportunities,CustomerRepository customers,CrmAccessService access,AuditService audit){this.followUps=followUps;this.opportunities=opportunities;this.customers=customers;this.access=access;this.audit=audit;}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping public ApiResponse<List<FollowUpView>> list(@RequestParam Long customerId){return ApiResponse.ok(followUps.findByCustomerOrderByFollowUpAtDesc(access.customer(customerId)).stream().map(FollowUpView::from).toList());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/recent") public ApiResponse<List<FollowUpView>> recent(){var user=access.current();return ApiResponse.ok(followUps.findTop10ByCreatorOrderByFollowUpAtDesc(user).stream().filter(item->access.canViewAll(user)||item.getCustomer().getOwner().getId().equals(user.getId())).map(FollowUpView::from).toList());}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping @Transactional public ApiResponse<FollowUpView> create(@Valid @RequestBody FollowUpRequest r){Customer customer=access.customer(r.customerId());Opportunity opportunity=null;if(r.opportunityId()!=null){opportunity=opportunities.findById(r.opportunityId()).orElseThrow(()->new BusinessException("商机不存在"));if(!opportunity.getCustomer().getId().equals(customer.getId()))throw new BusinessException("商机不属于该客户");}var item=new FollowUp(customer,opportunity,access.current(),r.method(),r.content(),r.followUpAt()==null?LocalDateTime.now():r.followUpAt(),r.nextAction(),r.nextFollowUpDate());customer.setNextFollowUpDate(r.nextFollowUpDate());customers.save(customer);followUps.save(item);audit.record("FOLLOW_UP_CREATE","FOLLOW_UP",item.getId(),null);return ApiResponse.ok("跟进记录已保存",FollowUpView.from(item));}
}
