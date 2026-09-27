/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.*;
import cn.zhuatech.crm.dto.CrmDto.*;
import cn.zhuatech.crm.model.Opportunity;
import cn.zhuatech.crm.repository.OpportunityRepository;
import cn.zhuatech.crm.service.CrmAccessService;
import cn.zhuatech.crm.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 提供受客户数据权限约束的商机查询、创建和阶段调整接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController @RequestMapping("/api/opportunities")
public class OpportunityController {
    private final OpportunityRepository opportunities; private final CrmAccessService access; private final AuditService audit;

    public OpportunityController(OpportunityRepository opportunities,CrmAccessService access,AuditService audit){this.opportunities=opportunities;this.access=access;this.audit=audit;}
    /**
     * 列出符合访问范围及筛选条件的业务记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @GetMapping public ApiResponse<List<OpportunityView>> list(@RequestParam(required=false) Long customerId){
        if(customerId!=null)return ApiResponse.ok(opportunities.findByCustomerOrderByUpdatedAtDesc(access.customer(customerId)).stream().map(OpportunityView::from).toList());
        var user=access.current();var items=access.canViewAll(user)?opportunities.findAllByOrderByUpdatedAtDesc():opportunities.findByOwnerOrderByUpdatedAtDesc(user);return ApiResponse.ok(items.stream().map(OpportunityView::from).toList());
    }
    /**
     * 校验业务请求并持久化新增记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping @Transactional public ApiResponse<OpportunityView> create(@Valid @RequestBody OpportunityRequest r){var customer=access.customer(r.customerId());var item=new Opportunity(customer,access.current(),r.name(),r.amount(),r.stage(),r.probability(),r.expectedCloseDate(),r.nextStep());opportunities.save(item);audit.record("OPPORTUNITY_CREATE","OPPORTUNITY",item.getId(),null);return ApiResponse.ok("商机已创建",OpportunityView.from(item));}
    /**
     * 校验商机访问权限并调整阶段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PatchMapping("/{id}/stage") @Transactional public ApiResponse<OpportunityView> stage(@PathVariable Long id,@Valid @RequestBody OpportunityStageRequest r){var item=opportunities.findById(id).orElseThrow(()->new BusinessException("商机不存在"));access.customer(item.getCustomer().getId());item.changeStage(r.stage(),r.probability(),r.nextStep());audit.record("OPPORTUNITY_STAGE","OPPORTUNITY",id,"阶段："+r.stage().name());return ApiResponse.ok("商机阶段已更新",OpportunityView.from(item));}
}
