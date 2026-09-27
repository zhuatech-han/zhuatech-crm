/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.service.CustomerAccountMergeGovernanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 评估客户主数据合并风险和所需条件，不执行自动合并的受认证接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController
@RequestMapping("/api/enterprise/crm")
public class CustomerAccountMergeGovernanceController {
    private final CustomerAccountMergeGovernanceService service;

    public CustomerAccountMergeGovernanceController(CustomerAccountMergeGovernanceService service) { this.service = service; }

    /**
     * 依据输入和治理规则返回评估结果，不自动执行第三方业务。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/customer-account-merge")
    public ApiResponse<CustomerAccountMergeGovernanceService.Assessment> assess(
            @Valid @RequestBody CustomerAccountMergeGovernanceService.Request request) {
        return ApiResponse.ok("客户主数据合并评估完成", service.assess(request));
    }
}
