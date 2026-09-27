/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 按业务规则计算线索评分和优先级。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Service
public class LeadScoringService {
    /**
     * 按线索规则计算评分和优先级。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public Result score(Request request) {
        int base = (int) Math.round(request.fitScore() * 0.35 + request.intentScore() * 0.30
            + request.engagementScore() * 0.25);
        int bonus = (request.budgetConfirmed() ? 5 : 0) + (request.decisionMakerConnected() ? 5 : 0);
        int inactivityPenalty = Math.min(20, request.daysInactive() / 7 * 3);
        int total = Math.max(0, Math.min(100, base + bonus - inactivityPenalty));
        String grade = total >= 75 ? "HOT" : total >= 50 ? "WARM" : "COLD";
        List<String> actions = new ArrayList<>();
        if (!request.budgetConfirmed()) actions.add("确认采购预算与资金审批节点");
        if (!request.decisionMakerConnected()) actions.add("补充决策链并预约关键人沟通");
        if (request.daysInactive() >= 14) actions.add("执行沉默线索唤醒并校验真实需求");
        if ("HOT".equals(grade)) actions.add("在二十四小时内安排方案演示与报价");
        else if ("WARM".equals(grade)) actions.add("进入重点培育序列并补齐商机信息");
        else actions.add("转入低频内容培育池");
        return new Result(request.leadCode(), total, grade, inactivityPenalty, actions);
    }

    /**
     * 封装 Request 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Request(@NotBlank String leadCode,
                          @Min(0) @Max(100) int fitScore,
                          @Min(0) @Max(100) int intentScore,
                          @Min(0) @Max(100) int engagementScore,
                          @Min(0) int daysInactive,
                          boolean budgetConfirmed, boolean decisionMakerConnected) {}

    /**
     * 封装 Result 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Result(String leadCode, int score, String grade, int inactivityPenalty,
                         List<String> recommendedActions) {}
}
