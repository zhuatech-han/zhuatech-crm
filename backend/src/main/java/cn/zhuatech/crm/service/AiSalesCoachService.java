/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import cn.zhuatech.crm.ai.OpenAiCompatibleGateway;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 组合本地销售规则和可选模型适配器生成跟进建议。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Service
public class AiSalesCoachService {
    private final OpenAiCompatibleGateway gateway;

    public AiSalesCoachService(OpenAiCompatibleGateway gateway) { this.gateway = gateway; }

    /**
     * 使用本地规则及可选模型生成销售辅导建议。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public Result coach(Request request) {
        int risk = 10;
        List<String> nextActions = new ArrayList<>();
        if (request.daysInStage() > 30) { risk += 25; nextActions.add("与客户确认决策流程和明确的推进日期"); }
        if (request.lastContactDays() > 7) { risk += 20; nextActions.add("在 24 小时内完成一次有价值的客户触达"); }
        if (request.stakeholderCount() < 2) { risk += 15; nextActions.add("识别业务、技术和采购侧关键关系人"); }
        if (Boolean.TRUE.equals(request.competitorMentioned())) { risk += 15; nextActions.add("准备差异化价值证明和竞争应对材料"); }
        if (!Boolean.TRUE.equals(request.nextMeetingScheduled())) { risk += 15; nextActions.add("锁定下一次会议及双方准备事项"); }
        risk += Math.min(20, request.objections().size() * 5);
        risk = Math.min(100, risk);
        if (nextActions.isEmpty()) nextActions.add("按当前节奏推进并持续验证客户价值");

        String context = "商机=%s，阶段=%s，阶段停留=%d天，最近联系=%d天，异议=%s，建议动作=%s"
            .formatted(request.opportunityName(), request.stage(), request.daysInStage(), request.lastContactDays(),
                request.objections(), nextActions);
        var enhanced = gateway.complete("你是 B2B 销售教练，请给出下一次沟通策略、提问清单和异议处理话术。", context);
        var metadata = gateway.metadata();
        String localAdvice = "当前商机风险 %d 分，优先执行：%s".formatted(risk, nextActions.getFirst());
        return new Result(risk, risk >= 70 ? "HIGH" : risk >= 40 ? "MEDIUM" : "LOW",
            enhanced.orElse(localAdvice), List.copyOf(nextActions),
            enhanced.isPresent() ? "EXTERNAL_MODEL" : "LOCAL_RULES", metadata.provider(), metadata.model());
    }

    /**
     * 封装 Request 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Request(@NotBlank String opportunityName, @NotBlank String stage, @Min(0) int daysInStage,
                          @Min(0) int lastContactDays, @Min(0) int stakeholderCount,
                          @NotNull Boolean competitorMentioned, @NotNull List<@NotBlank String> objections,
                          @NotNull Boolean nextMeetingScheduled) {}
    /**
     * 封装 Result 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Result(int riskScore, String riskLevel, String coachingAdvice, List<String> nextActions,
                         String aiMode, String provider, String model) {}
}
