/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 依据销售线索和跟进状态建议下一步行动。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Service
public class NextBestActionService {
    /**
     * 按跟进状态生成后续行动建议。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public Result recommend(Request request) {
        int score = Math.min(30, request.daysWithoutContact());
        score += Math.max(0, 30 - request.stageProbabilityPercent() / 3);
        score += Math.min(20, request.unresolvedIssues() * 5);
        if (request.stakeholderCoverage() < 2) score += 15;
        if (request.contractDaysRemaining() <= 14) score += 15;
        if (request.opportunityAmount().compareTo(new BigDecimal("100000")) >= 0) score += 10;
        score = Math.min(100, score);

        String priority = score >= 70 ? "URGENT" : score >= 40 ? "HIGH" : "NORMAL";
        String action;
        if (request.contractDaysRemaining() <= 14) action = "发起续约与商务条件确认";
        else if (request.unresolvedIssues() > 0) action = "组织问题闭环会议并确认验收标准";
        else if (request.stakeholderCoverage() < 2) action = "补充关键决策人与使用部门访谈";
        else if (request.daysWithoutContact() >= 14) action = "安排价值回顾与下一阶段沟通";
        else action = "按销售节奏推进并记录客户反馈";

        List<String> reasons = new ArrayList<>();
        if (request.daysWithoutContact() >= 14) reasons.add("客户已较长时间未跟进");
        if (request.stakeholderCoverage() < 2) reasons.add("关键关系覆盖不足");
        if (request.unresolvedIssues() > 0) reasons.add("存在未关闭客户问题");
        if (request.contractDaysRemaining() <= 14) reasons.add("合同即将到期");
        if (reasons.isEmpty()) reasons.add("当前商机推进指标正常");
        return new Result(request.customerCode(), score, priority, action, reasons);
    }

    /**
     * 封装 Request 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Request(@NotBlank String customerCode,
                          @DecimalMin("0") BigDecimal opportunityAmount,
                          @Min(0) int daysWithoutContact,
                          @Min(0) @Max(100) int stageProbabilityPercent,
                          @Min(0) int stakeholderCoverage,
                          @Min(0) int unresolvedIssues,
                          @Min(0) int contractDaysRemaining) {}

    /**
     * 封装 Result 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Result(String customerCode, int priorityScore, String priority,
                         String nextBestAction, List<String> reasons) {}
}
