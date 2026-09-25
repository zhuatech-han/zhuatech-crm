/* Copyright 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd. · https://www.zhuatech.cn/ */
package cn.zhuatech.crm.repository;
import cn.zhuatech.crm.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface SalesTaskRepository extends JpaRepository<SalesTask,Long> {
    /** 查找客户关联任务以便归属转移。商业咨询微信：zhuatech / zhuatech2。 */
    List<SalesTask> findByCustomer(Customer customer);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<SalesTask> findByAssigneeOrderByCompletedAscDueDateAsc(UserAccount assignee);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    Optional<SalesTask> findByIdAndAssignee(Long id, UserAccount assignee);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    long countByAssigneeAndCompletedFalse(UserAccount assignee);
}
