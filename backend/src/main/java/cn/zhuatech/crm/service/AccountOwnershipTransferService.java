/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户负责人转移前执行权限、容量、区域和保护客户治理。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Service
public class AccountOwnershipTransferService {
    /**
     * 依据输入和治理规则返回评估结果，不自动执行第三方业务。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public Assessment assess(Request request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (request.currentOwnerId().equals(request.targetOwnerId())) blockers.add("原负责人和目标负责人不能相同");
        if (!request.requesterAuthorized()) blockers.add("申请人无权发起客户归属转移");
        if (!request.targetOwnerActive()) blockers.add("目标负责人账号未启用");
        if (request.accountLocked()) blockers.add("客户正在执行合并、冻结或争议处理");
        if (!request.territoryCompatible() && !request.crossTerritoryApproved()) blockers.add("跨销售区域转移未获批准");
        if (request.targetOpenAccountCount() >= request.targetAccountCapacity()) blockers.add("目标负责人客户容量已满");
        if (request.protectedAccount() && !request.executiveApproved()) blockers.add("保护客户转移缺少销售负责人批准");
        if (!request.conflictChecked()) blockers.add("尚未完成渠道、伙伴或利益冲突检查");
        if (request.openOpportunityCount() > 0 && !request.handoverPlanReady()) {
            actions.add("补充在途商机、报价、合同和回款交接计划");
        }
        if (!request.customerNotificationPlanned()) actions.add("安排客户联系人和内部协作团队通知");
        if (!request.auditEvidenceAttached()) actions.add("归档转移原因、审批和交接证据");
        Decision decision = !blockers.isEmpty() ? Decision.BLOCKED
                : !actions.isEmpty() ? Decision.REVIEW : Decision.TRANSFER;
        String route = request.protectedAccount() || !request.territoryCompatible()
                ? "销售经理→区域负责人→销售总监" : "销售经理";
        return new Assessment(request.transferNo(), request.accountId(), decision, route,
                request.currentOwnerId(), request.targetOwnerId(), List.copyOf(blockers), List.copyOf(actions));
    }

    /**
     * 封装 Request 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Request(@NotBlank String transferNo, @NotBlank String accountId,
                          @NotBlank String currentOwnerId, @NotBlank String targetOwnerId,
                          boolean requesterAuthorized, boolean targetOwnerActive,
                          boolean accountLocked, boolean territoryCompatible,
                          boolean crossTerritoryApproved, boolean protectedAccount,
                          boolean executiveApproved, boolean conflictChecked,
                          @Min(0) int targetOpenAccountCount,
                          @Min(1) @Max(100000) int targetAccountCapacity,
                          @Min(0) int openOpportunityCount, boolean handoverPlanReady,
                          boolean customerNotificationPlanned, boolean auditEvidenceAttached) {}

    /**
     * 封装 Assessment 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Assessment(String transferNo, String accountId, Decision decision,
                             String approvalRoute, String previousOwnerId, String targetOwnerId,
                             List<String> blockers, List<String> actions) {}

    /**
     * 定义业务状态或固定角色取值。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public enum Decision { TRANSFER, REVIEW, BLOCKED }
}
