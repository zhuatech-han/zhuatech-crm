/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.repository;
import cn.zhuatech.crm.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
/**
 * 定义跟进持久化查询，供业务服务在完成权限校验后调用。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
public interface FollowUpRepository extends JpaRepository<FollowUp,Long> {

    List<FollowUp> findByCustomerOrderByFollowUpAtDesc(Customer customer);

    List<FollowUp> findTop10ByCreatorOrderByFollowUpAtDesc(UserAccount creator);
}
