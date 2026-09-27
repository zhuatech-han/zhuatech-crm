/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

/**
 * 根据报价风险和审批规则给出治理评估，不代替真实企业审批。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Service
public class QuotationApprovalGovernanceService {
    /**
     * 依据输入和治理规则返回评估结果，不自动执行第三方业务。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public Assessment assess(Request r) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (!r.customerVerified() || !r.opportunityQualified()) blockers.add("客户或商机尚未完成有效性核验");
        if (!r.catalogVersionValid()) blockers.add("报价使用的产品目录或价格版本无效");
        if (r.discountRate().compareTo(r.authorizedDiscountRate()) > 0) blockers.add("折扣超过当前人员授权上限");
        if (r.grossMarginRate().compareTo(r.minimumMarginRate()) < 0) blockers.add("预计毛利率低于业务底线");
        if (!r.currencyAndTaxConfirmed()) blockers.add("币种、税率或含税口径未确认");
        if (!r.customerCreditPassed()) blockers.add("客户信用检查未通过");
        if (r.nonStandardTerms() && !r.legalReviewed()) blockers.add("非标准商务条款必须经过法务复核");
        if (r.ownerId().equals(r.approverId())) blockers.add("报价负责人不得审批自己的越权报价");
        if (!r.auditEvidenceAttached()) actions.add("补充成本测算、审批依据及报价版本证据");
        if (!r.followUpScheduled()) actions.add("设置报价有效期内的跟进任务");
        RiskLevel risk = r.nonStandardTerms() || r.discountRate().compareTo(new BigDecimal("0.20")) > 0 ? RiskLevel.HIGH : RiskLevel.NORMAL;
        Decision decision = !blockers.isEmpty() ? Decision.BLOCKED : !actions.isEmpty() ? Decision.ESCALATE : Decision.APPROVE;
        String route = risk == RiskLevel.HIGH ? "销售经理→财务BP→法务/销售总监" : "销售经理";
        return new Assessment(r.quotationNo(), decision, risk, route, List.copyOf(blockers), List.copyOf(actions));
    }
    /**
     * 封装 Request 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Request(@NotBlank String quotationNo, @NotBlank String ownerId, @NotBlank String approverId,
                          @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal discountRate,
                          @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal authorizedDiscountRate,
                          @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal grossMarginRate,
                          @NotNull @DecimalMin("0.00") @DecimalMax("1.00") BigDecimal minimumMarginRate,
                          boolean customerVerified, boolean opportunityQualified, boolean catalogVersionValid,
                          boolean currencyAndTaxConfirmed, boolean customerCreditPassed, boolean nonStandardTerms,
                          boolean legalReviewed, boolean auditEvidenceAttached, boolean followUpScheduled) {}
    /**
     * 封装 Assessment 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Assessment(String quotationNo, Decision decision, RiskLevel riskLevel, String approvalRoute,
                             List<String> blockers, List<String> actions) {}
    /**
     * 定义业务状态或固定角色取值。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public enum Decision { APPROVE, ESCALATE, BLOCKED }
    /**
     * 定义业务状态或固定角色取值。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public enum RiskLevel { NORMAL, HIGH }
}
