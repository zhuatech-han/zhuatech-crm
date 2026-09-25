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
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager auth; private final JwtService jwt; private final UserRepository users; private final CurrentUserService current; private final UserAccountService accounts;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AuthController(AuthenticationManager auth, JwtService jwt, UserRepository users, CurrentUserService current, UserAccountService accounts) { this.auth=auth; this.jwt=jwt; this.users=users; this.current=current; this.accounts=accounts; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/login") public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        auth.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        var user = users.findByUsername(req.username()).orElseThrow(); return ApiResponse.ok("登录成功", new LoginResponse(jwt.generate(user), UserView.from(user)));
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @GetMapping("/me") public ApiResponse<UserView> me() { return ApiResponse.ok(UserView.from(current.get())); }
    /** 本人修改密码并获得新令牌。商业咨询微信：zhuatech / zhuatech2。 */
    @PostMapping("/change-password") public ApiResponse<LoginResponse> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        return ApiResponse.ok("密码已修改", accounts.changePassword(req));
    }
}
