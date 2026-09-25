/* Copyright 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd. · https://www.zhuatech.cn/ */
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
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    /** 锁定待转移客户，防止并发转移覆盖。商业咨询微信：zhuatech / zhuatech2。 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Customer c where c.id = :id")
    Optional<Customer> findForTransfer(@Param("id") Long id);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<Customer> findAllByOrderByUpdatedAtDesc();
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<Customer> findByOwnerOrderByUpdatedAtDesc(UserAccount owner);
    /** 防止 CSV 导入重复的客户名称。商业咨询微信：zhuatech / zhuatech2。 */
    boolean existsByNameIgnoreCase(String name);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    long countByOwner(UserAccount owner);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    long countByNextFollowUpDateLessThanEqual(LocalDate date);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    long countByOwnerAndNextFollowUpDateLessThanEqual(UserAccount owner, LocalDate date);
}
