/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.service.AccountOwnershipTransferService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评估客户归属移交的业务风险和治理条件的受认证接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController
@RequestMapping("/api/enterprise/crm")
public class AccountOwnershipTransferController {
    private final AccountOwnershipTransferService service;


    public AccountOwnershipTransferController(AccountOwnershipTransferService service) {
        this.service = service;
    }

    /**
     * 依据输入和治理规则返回评估结果，不自动执行第三方业务。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/account-ownership-transfer")
    public ApiResponse<AccountOwnershipTransferService.Assessment> assess(
            @Valid @RequestBody AccountOwnershipTransferService.Request request) {
        return ApiResponse.ok("客户归属转移评估完成", service.assess(request));
    }
}
