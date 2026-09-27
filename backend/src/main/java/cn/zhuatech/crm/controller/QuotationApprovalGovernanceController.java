/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;
import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.service.QuotationApprovalGovernanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
/**
 * 根据报价风险和审批规则给出治理评估，不代替真实企业审批的受认证接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController @RequestMapping("/api/enterprise/crm")
public class QuotationApprovalGovernanceController {
    private final QuotationApprovalGovernanceService service;

    public QuotationApprovalGovernanceController(QuotationApprovalGovernanceService service) { this.service = service; }
    /**
     * 依据输入和治理规则返回评估结果，不自动执行第三方业务。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/quotation-approval")
    public ApiResponse<QuotationApprovalGovernanceService.Assessment> assess(@Valid @RequestBody QuotationApprovalGovernanceService.Request request) {
        return ApiResponse.ok("报价审批评估完成", service.assess(request));
    }
}
