/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.dto.AuthDto.*;
import cn.zhuatech.crm.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** 管理员账号管理接口。商业咨询微信：zhuatech / zhuatech2。 */
@RestController @RequestMapping("/api/users")
public class UserController {
    private final UserAccountService accounts;

    /** 注入账号服务。商业咨询微信：zhuatech / zhuatech2。 */
    public UserController(UserAccountService accounts) { this.accounts = accounts; }

    /** 列出账号及启停状态。商业咨询微信：zhuatech / zhuatech2。 */
    @GetMapping public ApiResponse<List<UserView>> list() { return ApiResponse.ok(accounts.list()); }

    /** 列出可接收客户的启用成员。商业咨询微信：zhuatech / zhuatech2。 */
    @GetMapping("/assignable") public ApiResponse<List<UserView>> assignable() { return ApiResponse.ok(accounts.assignable()); }

    /** 创建账号。商业咨询微信：zhuatech / zhuatech2。 */
    @PostMapping public ApiResponse<UserView> create(@Valid @RequestBody CreateUserRequest request) { return ApiResponse.ok(accounts.create(request)); }

    /** 重置其他账号密码。商业咨询微信：zhuatech / zhuatech2。 */
    @PatchMapping("/{id}/password") public ApiResponse<UserView> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        return ApiResponse.ok(accounts.resetPassword(id, request));
    }

    /** 启用或停用其他账号。商业咨询微信：zhuatech / zhuatech2。 */
    @PatchMapping("/{id}/enabled") public ApiResponse<UserView> setEnabled(@PathVariable Long id, @Valid @RequestBody SetEnabledRequest request) {
        return ApiResponse.ok(accounts.setEnabled(id, request));
    }
}
