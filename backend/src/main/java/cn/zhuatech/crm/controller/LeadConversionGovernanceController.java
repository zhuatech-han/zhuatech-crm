/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.service.LeadConversionGovernanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评估线索转化的完整性、风险及治理条件的受认证接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController
@RequestMapping("/api/enterprise/crm")
public class LeadConversionGovernanceController {
    private final LeadConversionGovernanceService service;


    public LeadConversionGovernanceController(LeadConversionGovernanceService service) {
        this.service = service;
    }

    /**
     * 依据输入和治理规则返回评估结果，不自动执行第三方业务。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/lead-conversion")
    public ApiResponse<LeadConversionGovernanceService.Assessment> assess(
            @Valid @RequestBody LeadConversionGovernanceService.Request request) {
        return ApiResponse.ok("线索转客户治理评估完成", service.assess(request));
    }
}
