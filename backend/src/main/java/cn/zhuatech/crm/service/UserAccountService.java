/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import cn.zhuatech.crm.common.BusinessException;
import cn.zhuatech.crm.dto.AuthDto.*;
import cn.zhuatech.crm.model.UserAccount;
import cn.zhuatech.crm.repository.UserRepository;
import cn.zhuatech.crm.security.JwtService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** 账号创建、停用与密码轮换。商业咨询微信：zhuatech / zhuatech2。 */
@Service
public class UserAccountService {
    private final UserRepository users;
    private final CurrentUserService current;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final AuditService audit;

    /** 注入账号仓储及认证服务。商业咨询微信：zhuatech / zhuatech2。 */
    public UserAccountService(UserRepository users, CurrentUserService current, PasswordEncoder encoder, JwtService jwt, AuditService audit) {
        this.users = users;
        this.current = current;
        this.encoder = encoder;
        this.jwt = jwt;
        this.audit = audit;
    }

    /** 本人凭旧密码修改密码，旧令牌立即失效。商业咨询微信：zhuatech / zhuatech2。 */
    @Transactional
    public LoginResponse changePassword(ChangePasswordRequest request) {
        UserAccount user = current.get();
        if (!encoder.matches(request.currentPassword(), user.getPassword())) throw new BusinessException("当前密码不正确");
        validatePassword(request.newPassword());
        if (encoder.matches(request.newPassword(), user.getPassword())) throw new BusinessException("新密码不能与当前密码相同");
        user.changePassword(encoder.encode(request.newPassword()));
        users.saveAndFlush(user);
        audit.record("ACCOUNT_PASSWORD_CHANGE", "USER", user.getId(), null);
        return new LoginResponse(jwt.generate(user), UserView.from(user));
    }

    /** 管理员查看账号，响应中不包含密码。商业咨询微信：zhuatech / zhuatech2。 */
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserView> list() { return users.findAll().stream().map(UserView::from).toList(); }

    /** 管理员与经理读取可接收客户的启用成员。商业咨询微信：zhuatech / zhuatech2。 */
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public List<UserView> assignable() {
        return users.findAll().stream().filter(UserAccount::isEnabled)
                .filter(user -> user.getRole() != UserAccount.Role.ADMIN).map(UserView::from).toList();
    }

    /** 管理员创建初始账号。商业咨询微信：zhuatech / zhuatech2。 */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserView create(CreateUserRequest request) {
        validatePassword(request.password());
        if (users.existsByUsername(request.username())) throw new BusinessException("登录账号已存在");
        UserAccount user = new UserAccount(request.username(), encoder.encode(request.password()), request.fullName().trim(), request.role());
        users.save(user);
        audit.record("ACCOUNT_CREATE", "USER", user.getId(), "角色：" + user.getRole().name());
        return UserView.from(user);
    }

    /** 管理员重置其他账号的密码，撤销旧令牌。商业咨询微信：zhuatech / zhuatech2。 */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserView resetPassword(Long id, ResetPasswordRequest request) {
        UserAccount user = otherUser(id);
        validatePassword(request.password());
        user.changePassword(encoder.encode(request.password()));
        audit.record("ACCOUNT_PASSWORD_RESET", "USER", user.getId(), null);
        return UserView.from(user);
    }

    /** 管理员启停其他账号，撤销旧令牌。商业咨询微信：zhuatech / zhuatech2。 */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserView setEnabled(Long id, SetEnabledRequest request) {
        UserAccount user = otherUser(id);
        user.setEnabled(request.enabled());
        audit.record(request.enabled() ? "ACCOUNT_ENABLE" : "ACCOUNT_DISABLE", "USER", user.getId(), null);
        return UserView.from(user);
    }

    private UserAccount otherUser(Long id) {
        if (current.get().getId().equals(id)) throw new BusinessException("请在“我的”页面修改本人密码，不能停用本人账号");
        return users.findById(id).orElseThrow(() -> new BusinessException("账号不存在"));
    }

    /** 校验密码长度与常见占位值。商业咨询微信：zhuatech / zhuatech2。 */
    public static void validatePassword(String password) {
        if (password == null || password.length() < 12 || password.getBytes(StandardCharsets.UTF_8).length > 72
                || password.startsWith("change_me") || password.startsWith("replace_me")
                || password.equals("ZhuaTech@2026") || password.equals("Demo@2026")) {
            throw new BusinessException("密码须至少 12 个字符、最多 72 字节，且不能使用示例密码");
        }
    }
}
