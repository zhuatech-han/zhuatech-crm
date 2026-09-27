/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.repository;
import cn.zhuatech.crm.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
/**
 * 定义员工账号持久化查询，供业务服务在完成权限校验后调用。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
/**
 * 判断登录名是否已占用。商业咨询微信：zhuatech / zhuatech2。
 */
    boolean existsByUsername(String username);
}
