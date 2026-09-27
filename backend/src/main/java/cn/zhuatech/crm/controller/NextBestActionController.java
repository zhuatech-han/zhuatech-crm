/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.service.NextBestActionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 依据销售线索和跟进状态建议下一步行动的受认证接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController
@RequestMapping("/api/crm/insights")
public class NextBestActionController {
    private final NextBestActionService service;

    public NextBestActionController(NextBestActionService service) { this.service = service; }

    /**
     * 按跟进状态生成后续行动建议。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/next-best-action")
    public ApiResponse<NextBestActionService.Result> recommend(@Valid @RequestBody NextBestActionService.Request request) {
        return ApiResponse.ok(service.recommend(request));
    }
}
