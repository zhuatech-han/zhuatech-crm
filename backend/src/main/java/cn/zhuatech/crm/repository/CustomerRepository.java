/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.repository;
import cn.zhuatech.crm.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
/**
 * 定义客户持久化查询，供业务服务在完成权限校验后调用。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    /**
     * 锁定待转移客户，防止并发转移覆盖。商业咨询微信：zhuatech / zhuatech2。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Customer c where c.id = :id")
    Optional<Customer> findForTransfer(@Param("id") Long id);

    List<Customer> findAllByOrderByUpdatedAtDesc();

    List<Customer> findByOwnerOrderByUpdatedAtDesc(UserAccount owner);
    /**
     * 防止 CSV 导入重复的客户名称。商业咨询微信：zhuatech / zhuatech2。
     */
    boolean existsByNameIgnoreCase(String name);

    long countByOwner(UserAccount owner);

    long countByNextFollowUpDateLessThanEqual(LocalDate date);

    long countByOwnerAndNextFollowUpDateLessThanEqual(UserAccount owner, LocalDate date);
}
