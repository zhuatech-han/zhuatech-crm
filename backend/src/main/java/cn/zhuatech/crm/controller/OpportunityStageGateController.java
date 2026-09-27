/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.service.OpportunityStageGateService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 检查商机阶段推进的前置条件及风险的受认证接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController
@RequestMapping("/api/enterprise/crm")
public class OpportunityStageGateController {
    private final OpportunityStageGateService service;

    public OpportunityStageGateController(OpportunityStageGateService service) { this.service = service; }

    /**
     * 依据输入和治理规则返回评估结果，不自动执行第三方业务。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/opportunity-stage-gate")
    public ApiResponse<OpportunityStageGateService.Assessment> assess(
            @Valid @RequestBody OpportunityStageGateService.Request request) {
        return ApiResponse.ok("商机阶段门禁评估完成", service.assess(request));
    }
}
