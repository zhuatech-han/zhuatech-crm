/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.dto.CrmDto.DashboardView;
import cn.zhuatech.crm.model.Opportunity;
import cn.zhuatech.crm.repository.*;
import cn.zhuatech.crm.service.CrmAccessService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 聚合当前用户权限范围内的客户、商机和销售任务指标。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController @RequestMapping("/api/dashboard")
public class DashboardController {
    private final CustomerRepository customers; private final OpportunityRepository opportunities; private final SalesTaskRepository tasks; private final CrmAccessService access;

    public DashboardController(CustomerRepository customers,OpportunityRepository opportunities,SalesTaskRepository tasks,CrmAccessService access){this.customers=customers;this.opportunities=opportunities;this.tasks=tasks;this.access=access;}
    /**
     * 统计当前账号可见范围内的客户、商机和待办指标。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @GetMapping public ApiResponse<DashboardView> dashboard(){
        var user=access.current();
        var ops=access.canViewAll(user)?opportunities.findAllByOrderByUpdatedAtDesc():opportunities.findByOwnerOrderByUpdatedAtDesc(user);
        var active=ops.stream().filter(o->o.getStage()!=Opportunity.Stage.WON&&o.getStage()!=Opportunity.Stage.LOST).toList();
        BigDecimal pipeline=active.stream().map(Opportunity::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        long customerCount=access.canViewAll(user)?customers.count():customers.countByOwner(user);
        long due=access.canViewAll(user)?customers.countByNextFollowUpDateLessThanEqual(LocalDate.now()):customers.countByOwnerAndNextFollowUpDateLessThanEqual(user,LocalDate.now());
        return ApiResponse.ok(new DashboardView(customerCount,active.size(),pipeline,due,tasks.countByAssigneeAndCompletedFalse(user)));
    }
}
