/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import cn.zhuatech.crm.common.BusinessException;
import cn.zhuatech.crm.dto.CrmDto.CustomerView;
import cn.zhuatech.crm.model.Customer;
import cn.zhuatech.crm.model.UserAccount;
import cn.zhuatech.crm.repository.CustomerRepository;
import cn.zhuatech.crm.repository.OpportunityRepository;
import cn.zhuatech.crm.repository.SalesTaskRepository;
import cn.zhuatech.crm.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 实际转移客户及关联业务归属，保留跟进创建人历史。商业咨询微信：zhuatech / zhuatech2。 */
@Service
public class CustomerTransferService {
    private final CustomerRepository customers;
    private final OpportunityRepository opportunities;
    private final SalesTaskRepository tasks;
    private final UserRepository users;
    private final AuditService audit;

    public CustomerTransferService(CustomerRepository customers, OpportunityRepository opportunities,
                                   SalesTaskRepository tasks, UserRepository users, AuditService audit) {
        this.customers = customers;
        this.opportunities = opportunities;
        this.tasks = tasks;
        this.users = users;
        this.audit = audit;
    }

    /** 经理或管理员在单一事务内转移客户、商机、任务并记录原因。商业咨询微信：zhuatech / zhuatech2。 */
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Transactional
    public CustomerView transfer(Long customerId, Long ownerId, String reason) {
        Customer customer = customers.findForTransfer(customerId).orElseThrow(() -> new BusinessException("客户不存在"));
        UserAccount target = users.findById(ownerId).orElseThrow(() -> new BusinessException("目标成员不存在"));
        if (!target.isEnabled() || target.getRole() == UserAccount.Role.ADMIN) {
            throw new BusinessException("请选择启用中的销售人员或经理");
        }
        UserAccount previous = customer.getOwner();
        if (previous.getId().equals(target.getId())) throw new BusinessException("客户已属于该负责人");

        customer.setOwner(target);
        opportunities.findByCustomerOrderByUpdatedAtDesc(customer).forEach(item -> item.setOwner(target));
        tasks.findByCustomer(customer).forEach(item -> item.setAssignee(target));
        audit.record("CUSTOMER_TRANSFER", "CUSTOMER", customerId,
                previous.getUsername() + " → " + target.getUsername() + "；原因：" + reason.trim());
        customers.flush();
        return CustomerView.from(customer);
    }
}
