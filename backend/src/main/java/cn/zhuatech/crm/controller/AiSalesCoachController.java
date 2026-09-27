/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.service.AiSalesCoachService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 提供本地规则及可配置模型的销售辅导接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController
@RequestMapping("/api/crm/ai")
public class AiSalesCoachController {
    private final AiSalesCoachService service;

    public AiSalesCoachController(AiSalesCoachService service) { this.service = service; }
    /**
     * 使用本地规则及可选模型生成销售辅导建议。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/sales-coach")
    public ApiResponse<AiSalesCoachService.Result> coach(@Valid @RequestBody AiSalesCoachService.Request request) {
        return ApiResponse.ok(service.coach(request));
    }
}
