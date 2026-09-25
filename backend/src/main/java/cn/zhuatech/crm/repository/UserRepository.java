/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.repository;
import cn.zhuatech.crm.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface UserRepository extends JpaRepository<UserAccount, Long> { /**
                                                                            * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                            */
Optional<UserAccount> findByUsername(String username);
/** 判断登录名是否已占用。商业咨询微信：zhuatech / zhuatech2。 */
boolean existsByUsername(String username);
}
