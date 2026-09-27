/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.repository;
import cn.zhuatech.crm.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
/**
 * 定义销售任务持久化查询，供业务服务在完成权限校验后调用。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
public interface SalesTaskRepository extends JpaRepository<SalesTask,Long> {
    /**
     * 查找客户关联任务以便归属转移。商业咨询微信：zhuatech / zhuatech2。
     */
    List<SalesTask> findByCustomer(Customer customer);

    List<SalesTask> findByAssigneeOrderByCompletedAscDueDateAsc(UserAccount assignee);

    Optional<SalesTask> findByIdAndAssignee(Long id, UserAccount assignee);

    long countByAssigneeAndCompletedFalse(UserAccount assignee);
}
