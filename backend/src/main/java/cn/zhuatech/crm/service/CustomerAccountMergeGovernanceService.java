/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 评估客户主数据合并风险和所需条件，不执行自动合并。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Service
public class CustomerAccountMergeGovernanceService {
    /**
     * 依据输入和治理规则返回评估结果，不自动执行第三方业务。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public Assessment assess(Request request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (request.survivorAccountId().equals(request.duplicateAccountId())) blockers.add("保留客户与待合并客户不能相同");
        if (!request.legalEntityMatched()) blockers.add("客户法律主体或统一社会信用标识不一致");
        if (!request.dataOwnershipResolved()) blockers.add("客户负责人和数据归属冲突未解决");
        if (!request.contactConsentPreserved()) blockers.add("联系人授权及隐私同意无法完整继承");
        if (!request.openOpportunitiesReassigned()) blockers.add("未关闭商机尚未完成重新归属");
        if (!request.contractsReconciled()) blockers.add("合同及报价关系未完成核对");
        if (!request.receivablesReconciled()) blockers.add("应收、信用额度或回款关系未完成核对");
        if (!request.serviceCasesReassigned()) blockers.add("在途服务工单尚未重新关联");
        if (!request.marketingPreferencesPreserved()) blockers.add("退订、黑名单或触达偏好未保留");
        if (!request.fieldConflictsResolved()) blockers.add("客户主数据字段冲突未裁决");
        if (!request.businessOwnerApproved()) blockers.add("客户业务负责人尚未批准合并");
        if (!request.dataStewardApproved()) blockers.add("主数据管理员尚未批准合并");
        if (!request.makerCheckerSeparated()) blockers.add("合并经办人与复核人未职责分离");
        if (!request.auditReady()) blockers.add("匹配、裁决、审批及合并证据链不完整");
        if (!request.duplicateEvidenceArchived()) actions.add("归档重复客户匹配证据和置信依据");
        if (!request.downstreamSyncReady()) actions.add("准备订单、财务、服务等下游映射同步");
        if (!request.customerNoticeReady()) actions.add("按需准备客户联系人变更通知");
        Decision decision = !blockers.isEmpty() ? Decision.BLOCKED : !actions.isEmpty() ? Decision.REVIEW : Decision.MERGE;
        return new Assessment(request.mergeRequestId(), request.survivorAccountId(),
                request.duplicateAccountId(), decision, List.copyOf(blockers), List.copyOf(actions));
    }

    /**
     * 封装 Request 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Request(@NotBlank String mergeRequestId, @NotBlank String survivorAccountId,
                          @NotBlank String duplicateAccountId, boolean legalEntityMatched,
                          boolean dataOwnershipResolved, boolean contactConsentPreserved,
                          boolean openOpportunitiesReassigned, boolean contractsReconciled,
                          boolean receivablesReconciled, boolean serviceCasesReassigned,
                          boolean marketingPreferencesPreserved, boolean fieldConflictsResolved,
                          boolean businessOwnerApproved, boolean dataStewardApproved,
                          boolean makerCheckerSeparated, boolean auditReady,
                          boolean duplicateEvidenceArchived, boolean downstreamSyncReady,
                          boolean customerNoticeReady) {}
    /**
     * 封装 Assessment 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record Assessment(String mergeRequestId, String survivorAccountId, String duplicateAccountId,
                             Decision decision, List<String> blockers, List<String> actions) {}
    /**
     * 定义业务状态或固定角色取值。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public enum Decision { MERGE, REVIEW, BLOCKED }
}
