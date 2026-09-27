/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * 汇总销售管道的加权预测，并识别长时间未推进或临近关单的风险商机。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Service
public class OpportunityForecastService {
    /**
     * 计算商机风险及汇总销售预测。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public ForecastResult forecast(ForecastRequest request) {
        List<DealForecast> deals = request.deals().stream().map(this::evaluate)
            .sorted(Comparator.comparing(DealForecast::riskScore).reversed()
                .thenComparing(DealForecast::name)).toList();
        BigDecimal weighted = deals.stream().map(DealForecast::weightedAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal commit = deals.stream().filter(item -> "COMMIT".equals(item.category()))
            .map(DealForecast::weightedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal upside = deals.stream().filter(item -> "UPSIDE".equals(item.category()))
            .map(DealForecast::weightedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal coverage = request.quarterTarget().signum() == 0 ? BigDecimal.ZERO
            : weighted.divide(request.quarterTarget(), 4, RoundingMode.HALF_UP);
        long atRisk = deals.stream().filter(DealForecast::managerAttention).count();
        String guidance = coverage.compareTo(new BigDecimal("1.00")) >= 0 && atRisk == 0
            ? "预测覆盖目标，保持关键商机推进节奏"
            : coverage.compareTo(new BigDecimal("0.80")) < 0 ? "预测覆盖不足，需补充高质量商机"
            : "优先处理高风险商机并校准关单日期";
        return new ForecastResult(request.quarterTarget(), weighted, commit, upside, coverage,
            atRisk, deals, guidance);
    }

    /**
     * 按输入指标计算业务评估结果。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    private DealForecast evaluate(DealInput deal) {
        BigDecimal weighted = deal.amount().multiply(BigDecimal.valueOf(deal.probability()))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        long daysToClose = deal.expectedCloseDate() == null ? 999
            : Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deal.expectedCloseDate()));
        int risk = Math.min(100, deal.daysSinceActivity() * 2
            + (daysToClose <= 14 && deal.probability() < 60 ? 30 : 0)
            + (deal.criticalBlocker() ? 35 : 0));
        String category = deal.probability() >= 70 ? "COMMIT" : deal.probability() >= 40 ? "UPSIDE" : "PIPELINE";
        boolean attention = risk >= 50;
        String nextAction = deal.criticalBlocker() ? "48 小时内关闭关键阻塞项"
            : deal.daysSinceActivity() >= 14 ? "安排客户回访并更新下一步计划"
            : daysToClose <= 14 && deal.probability() < 60 ? "复核预计关单日期与决策链" : "按当前节奏推进";
        return new DealForecast(deal.name(), weighted, category, risk, attention, daysToClose, nextAction);
    }

    /**
     * 封装 DealInput 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record DealInput(@NotBlank String name, @DecimalMin("0") BigDecimal amount,
                            @Min(0) @Max(100) int probability, @NotBlank String stage,
                            LocalDate expectedCloseDate, @Min(0) int daysSinceActivity,
                            boolean criticalBlocker) {}
    /**
     * 封装 ForecastRequest 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record ForecastRequest(@DecimalMin("0") BigDecimal quarterTarget,
                                  @NotEmpty List<@Valid DealInput> deals) {}
    /**
     * 封装 DealForecast 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record DealForecast(String name, BigDecimal weightedAmount, String category,
                               int riskScore, boolean managerAttention, long daysToClose,
                               String nextAction) {}
    /**
     * 封装 ForecastResult 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record ForecastResult(BigDecimal quarterTarget, BigDecimal weightedForecast,
                                 BigDecimal commitForecast, BigDecimal upsideForecast,
                                 BigDecimal targetCoverage, long atRiskDeals,
                                 List<DealForecast> deals, String guidance) {}
}
