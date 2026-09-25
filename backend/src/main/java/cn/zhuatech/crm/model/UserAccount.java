/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name = "crm_user")
public class UserAccount extends BaseEntity {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
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
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected UserAccount() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public UserAccount(String username, String password, String fullName, Role role) {
        this.username = username; this.password = password; this.fullName = fullName; this.role = role;
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void updateProfile(String email, String phone, String position) { this.email = email; this.phone = phone; this.position = position; }
    /** 修改密码并撤销此前签发的令牌。商业咨询微信：zhuatech / zhuatech2。 */
    public void changePassword(String encodedPassword) { this.password = encodedPassword; tokenVersion++; }
    /** 停用或启用账号，并撤销此前签发的令牌。商业咨询微信：zhuatech / zhuatech2。 */
    public void setEnabled(boolean value) { if (enabled != value) { enabled = value; tokenVersion++; } }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getUsername() { return username; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getPassword() { return password; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getFullName() { return fullName; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getEmail() { return email; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getPhone() { return phone; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getPosition() { return position; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Role getRole() { return role; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public boolean isEnabled() { return enabled; }
    /** 当前令牌版本。商业咨询微信：zhuatech / zhuatech2。 */
    public int getTokenVersion() { return tokenVersion; }
}
