/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.dto.AuthDto.*;
import cn.zhuatech.crm.repository.UserRepository;
import cn.zhuatech.crm.security.JwtService;
import cn.zhuatech.crm.service.CurrentUserService;
import cn.zhuatech.crm.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

/**
 * 提供员工登录、当前账号查询和本人密码修改接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager auth; private final JwtService jwt; private final UserRepository users; private final CurrentUserService current; private final UserAccountService accounts;

    public AuthController(AuthenticationManager auth, JwtService jwt, UserRepository users, CurrentUserService current, UserAccountService accounts) { this.auth=auth; this.jwt=jwt; this.users=users; this.current=current; this.accounts=accounts; }
    /**
     * 校验账号密码并为有效账号签发登录凭证。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/login") public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        auth.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        var user = users.findByUsername(req.username()).orElseThrow(); return ApiResponse.ok("登录成功", new LoginResponse(jwt.generate(user), UserView.from(user)));
    }
    /**
     * 返回当前认证员工的非密码资料。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @GetMapping("/me") public ApiResponse<UserView> me() { return ApiResponse.ok(UserView.from(current.get())); }
    /**
     * 本人修改密码并获得新令牌。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping("/change-password") public ApiResponse<LoginResponse> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        return ApiResponse.ok("密码已修改", accounts.changePassword(req));
    }
}
