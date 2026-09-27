/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 按客户互动和业务指标评估客户健康状态。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Service
public class CustomerHealthService {
    /**
     * 按输入指标计算业务评估结果。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public Result evaluate(Request request) {
        int score = Math.max(0, Math.min(100, (int) Math.round(request.engagementScore() * .45
            + (1 - request.paymentRisk()) * 30 + Math.min(15, request.openOpportunities() * 5)
            - Math.min(20, request.inactiveDays()) - (request.criticalComplaint() ? 25 : 0))));
        String band = score >= 70 ? "HEALTHY" : score >= 40 ? "WATCH" : "RISK";
        List<String> actions = new ArrayList<>();
        if (request.inactiveDays() >= 14) actions.add("安排客户回访并确认下一步计划");
        if (request.paymentRisk() >= .5) actions.add("联合财务复核账期与信用额度");
        if (request.criticalComplaint()) actions.add("创建重大客诉闭环并由负责人跟进");
        if (actions.isEmpty()) actions.add("保持常规客户成功跟进节奏");
        return new Result(request.customerName(), score, band, !"HEALTHY".equals(band), actions);
    }

    /**
     * 封装 Request 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Request(@NotBlank String customerName, @Min(0) @Max(100) int engagementScore,
                          @DecimalMin("0") @DecimalMax("1") double paymentRisk,
                          @Min(0) int openOpportunities, @Min(0) int inactiveDays,
                          boolean criticalComplaint) {}
    /**
     * 封装 Result 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Result(String customerName, int healthScore, String band,
                         boolean managerReview, List<String> actions) {}
}
