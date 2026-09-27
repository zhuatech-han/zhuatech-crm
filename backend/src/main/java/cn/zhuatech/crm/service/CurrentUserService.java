/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;
import cn.zhuatech.crm.common.BusinessException;
import cn.zhuatech.crm.model.UserAccount;
import cn.zhuatech.crm.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
/**
 * 获取已认证的当前员工并拒绝无效请求身份。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Service
public class CurrentUserService {
    private final UserRepository users;

    public CurrentUserService(UserRepository users) { this.users = users; }
    /**
     * 获取当前认证员工。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public UserAccount get() { String name = SecurityContextHolder.getContext().getAuthentication().getName(); return users.findByUsername(name).orElseThrow(() -> new BusinessException("登录状态已失效")); }
}
