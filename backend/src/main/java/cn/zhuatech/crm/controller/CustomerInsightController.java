/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.service.CustomerHealthService;
import cn.zhuatech.crm.service.OpportunityForecastService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供客户健康评估和销售预测接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController
@RequestMapping("/api/customer-intelligence")
public class CustomerInsightController {
    private final CustomerHealthService service;
    private final OpportunityForecastService forecastService;

    public CustomerInsightController(CustomerHealthService service, OpportunityForecastService forecastService) {
        this.service = service;
        this.forecastService = forecastService;
    }

    /**
     * 按输入指标计算业务评估结果。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/health-score")
    public ApiResponse<CustomerHealthService.Result> evaluate(@Valid @RequestBody CustomerHealthService.Request request) {
        return ApiResponse.ok(service.evaluate(request));
    }

    /**
     * 计算商机风险及汇总销售预测。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/opportunity-forecast")
    public ApiResponse<OpportunityForecastService.ForecastResult> forecast(
        @Valid @RequestBody OpportunityForecastService.ForecastRequest request) {
        return ApiResponse.ok(forecastService.forecast(request));
    }
}
