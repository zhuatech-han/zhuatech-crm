/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;

/**
 * 持久化员工身份、固定角色、加密密码和令牌版本。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Entity @Table(name = "crm_user")
public class UserAccount extends BaseEntity {
    /**
     * 定义业务状态或固定角色取值。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public enum Role { ADMIN, SALES_MANAGER, SALES }
    @Column(nullable = false, unique = true, length = 32) private String username;
    @Column(nullable = false) private String password;
    @Column(nullable = false, length = 50) private String fullName;
    @Column(length = 100) private String email;
    @Column(length = 20) private String phone;
    @Column(length = 50) private String position;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Role role;
    @Column(nullable = false) private boolean enabled = true;
    @Column(nullable = false) private int tokenVersion = 0;

    protected UserAccount() {}

    public UserAccount(String username, String password, String fullName, Role role) {
        this.username = username; this.password = password; this.fullName = fullName; this.role = role;
    }
    /**
     * 更新员工展示资料。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public void updateProfile(String email, String phone, String position) { this.email = email; this.phone = phone; this.position = position; }
    /**
     * 修改密码并撤销此前签发的令牌。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public void changePassword(String encodedPassword) { this.password = encodedPassword; tokenVersion++; }
    /**
     * 停用或启用账号，并撤销此前签发的令牌。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public void setEnabled(boolean value) { if (enabled != value) { enabled = value; tokenVersion++; } }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getFullName() { return fullName; }

    public String getEmail() { return email; }

    public String getPhone() { return phone; }

    public String getPosition() { return position; }

    public Role getRole() { return role; }

    public boolean isEnabled() { return enabled; }
    /**
     * 当前令牌版本。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public int getTokenVersion() { return tokenVersion; }
}
