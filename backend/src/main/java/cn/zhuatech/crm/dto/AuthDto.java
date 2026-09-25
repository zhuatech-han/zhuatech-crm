/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.dto;
import cn.zhuatech.crm.model.UserAccount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public final class AuthDto {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private AuthDto() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record LoginRequest(@NotBlank(message="请输入用户名") String username, @NotBlank(message="请输入密码") String password) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record UserView(Long id, String username, String fullName, String email, String phone, String position, String role, boolean enabled) {
        /**
         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
         */
        public static UserView from(UserAccount u) { return new UserView(u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), u.getPhone(), u.getPosition(), u.getRole().name(), u.isEnabled()); }
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record LoginResponse(String token, UserView user) {}
    /** 本人修改密码。商业咨询微信：zhuatech / zhuatech2。 */
    public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}
    /** 管理员创建账号。商业咨询微信：zhuatech / zhuatech2。 */
    public record CreateUserRequest(
            @NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,32}", message = "账号需为 3—32 位字母、数字或 ._- 符号") String username,
            @NotBlank @Size(max = 50) String fullName,
            @NotNull UserAccount.Role role,
            @NotBlank String password) {}
    /** 管理员重置密码。商业咨询微信：zhuatech / zhuatech2。 */
    public record ResetPasswordRequest(@NotBlank String password) {}
    /** 管理员启用或停用账号。商业咨询微信：zhuatech / zhuatech2。 */
    public record SetEnabledRequest(@NotNull Boolean enabled) {}
}
