/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查商机阶段推进的前置条件及风险。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Service
public class OpportunityStageGateService {
    /**
     * 依据输入和治理规则返回评估结果，不自动执行第三方业务。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public Assessment assess(Request request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (!request.contactConsentValid()) blockers.add("客户联系授权无效或已撤回");
        if (!request.stageEvidenceComplete()) blockers.add("目标阶段所需证据不完整");
        if (request.discountBps() > request.authorizedDiscountBps()) blockers.add("折扣超过当前人员授权额度");
        if (!request.decisionMakerIdentified()) actions.add("确认客户决策人和采购流程");
        if (!request.nextActionScheduled()) actions.add("登记下一步动作、负责人和截止时间");
        if (!request.closeDateFeasible()) actions.add("复核预计成交日期与交付能力");

        Decision decision = !blockers.isEmpty() ? Decision.BLOCKED
                : !actions.isEmpty() ? Decision.REVIEW : Decision.ADVANCE;
        return new Assessment(request.opportunityId(), request.targetStage(), decision,
                List.copyOf(blockers), List.copyOf(actions));
    }

    /**
     * 封装 Request 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Request(
            @NotBlank String opportunityId,
            @NotBlank String targetStage,
            @Min(0) long amountCents,
            @Min(0) @Max(10000) int discountBps,
            @Min(0) @Max(10000) int authorizedDiscountBps,
            boolean contactConsentValid,
            boolean decisionMakerIdentified,
            boolean nextActionScheduled,
            boolean stageEvidenceComplete,
            boolean closeDateFeasible) {}

    /**
     * 封装 Assessment 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Assessment(String opportunityId, String targetStage, Decision decision,
                             List<String> blockers, List<String> actions) {}
    /**
     * 定义业务状态或固定角色取值。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public enum Decision { ADVANCE, REVIEW, BLOCKED }
}
