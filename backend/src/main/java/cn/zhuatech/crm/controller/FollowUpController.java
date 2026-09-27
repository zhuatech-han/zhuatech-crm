/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
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
 * 提供销售跟进查询、记录和近期记录接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController @RequestMapping("/api/follow-ups")
public class FollowUpController {
    private final FollowUpRepository followUps; private final OpportunityRepository opportunities; private final CustomerRepository customers; private final CrmAccessService access; private final AuditService audit;

    public FollowUpController(FollowUpRepository followUps,OpportunityRepository opportunities,CustomerRepository customers,CrmAccessService access,AuditService audit){this.followUps=followUps;this.opportunities=opportunities;this.customers=customers;this.access=access;this.audit=audit;}
    /**
     * 列出符合访问范围及筛选条件的业务记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @GetMapping public ApiResponse<List<FollowUpView>> list(@RequestParam Long customerId){return ApiResponse.ok(followUps.findByCustomerOrderByFollowUpAtDesc(access.customer(customerId)).stream().map(FollowUpView::from).toList());}
    /**
     * 查询当前访问范围内的近期记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @GetMapping("/recent") public ApiResponse<List<FollowUpView>> recent(){var user=access.current();return ApiResponse.ok(followUps.findTop10ByCreatorOrderByFollowUpAtDesc(user).stream().filter(item->access.canViewAll(user)||item.getCustomer().getOwner().getId().equals(user.getId())).map(FollowUpView::from).toList());}
    /**
     * 校验业务请求并持久化新增记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping @Transactional public ApiResponse<FollowUpView> create(@Valid @RequestBody FollowUpRequest r){Customer customer=access.customer(r.customerId());Opportunity opportunity=null;if(r.opportunityId()!=null){opportunity=opportunities.findById(r.opportunityId()).orElseThrow(()->new BusinessException("商机不存在"));if(!opportunity.getCustomer().getId().equals(customer.getId()))throw new BusinessException("商机不属于该客户");}var item=new FollowUp(customer,opportunity,access.current(),r.method(),r.content(),r.followUpAt()==null?LocalDateTime.now():r.followUpAt(),r.nextAction(),r.nextFollowUpDate());customer.setNextFollowUpDate(r.nextFollowUpDate());customers.save(customer);followUps.save(item);audit.record("FOLLOW_UP_CREATE","FOLLOW_UP",item.getId(),null);return ApiResponse.ok("跟进记录已保存",FollowUpView.from(item));}
}
