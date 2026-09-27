/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.service.LeadScoringService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 按业务规则计算线索评分和优先级的受认证接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController
@RequestMapping("/api/crm/insights")
public class LeadScoringController {
    private final LeadScoringService service;

    public LeadScoringController(LeadScoringService service) { this.service = service; }

    /**
     * 按线索规则计算评分和优先级。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/lead-score")
    public ApiResponse<LeadScoringService.Result> score(@Valid @RequestBody LeadScoringService.Request request) {
        return ApiResponse.ok(service.score(request));
    }
}
