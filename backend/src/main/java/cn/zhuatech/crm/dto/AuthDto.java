/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.dto;
import cn.zhuatech.crm.model.UserAccount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 定义登录、账号管理和密码变更的请求校验与安全响应结构。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
public final class AuthDto {

    private AuthDto() {}
    /**
     * 封装 LoginRequest 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record LoginRequest(@NotBlank(message="请输入用户名") String username, @NotBlank(message="请输入密码") String password) {}
    /**
     * 封装 UserView 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record UserView(Long id, String username, String fullName, String email, String phone, String position, String role, boolean enabled) {
        /**
         * 将业务实体转换为接口视图，避免返回加密密码等内部字段。
         *
         * Copyright 2026 上海如静知华信息科技有限公司
         * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
         */
        public static UserView from(UserAccount u) { return new UserView(u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), u.getPhone(), u.getPosition(), u.getRole().name(), u.isEnabled()); }
    }
    /**
     * 封装 LoginResponse 的业务输入或返回字段。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record LoginResponse(String token, UserView user) {}
    /**
     * 本人修改密码。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}
    /**
     * 管理员创建账号。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record CreateUserRequest(
            @NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,32}", message = "账号需为 3—32 位字母、数字或 ._- 符号") String username,
            @NotBlank @Size(max = 50) String fullName,
            @NotNull UserAccount.Role role,
            @NotBlank String password) {}
    /**
     * 管理员重置密码。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record ResetPasswordRequest(@NotBlank String password) {}
    /**
     * 管理员启用或停用账号。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public record SetEnabledRequest(@NotNull Boolean enabled) {}
}
