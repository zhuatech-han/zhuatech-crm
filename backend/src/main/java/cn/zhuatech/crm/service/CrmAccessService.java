/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import cn.zhuatech.crm.common.BusinessException;
import cn.zhuatech.crm.model.Customer;
import cn.zhuatech.crm.model.UserAccount;
import cn.zhuatech.crm.repository.CustomerRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/** 按销售角色限制客户数据访问。商业咨询微信：zhuatech / zhuatech2。 */
@Service
public class CrmAccessService {
    private final CurrentUserService current;
    private final CustomerRepository customers;

    /** 注入当前用户与客户仓储。商业咨询微信：zhuatech / zhuatech2。 */
    public CrmAccessService(CurrentUserService current, CustomerRepository customers) {
        this.current = current;
        this.customers = customers;
    }

    /** 返回当前登录账号。商业咨询微信：zhuatech / zhuatech2。 */
    public UserAccount current() { return current.get(); }

    /** 管理员和销售经理可查看所有客户。商业咨询微信：zhuatech / zhuatech2。 */
    public boolean canViewAll(UserAccount user) { return user.getRole() != UserAccount.Role.SALES; }

    /** 按编号取得有权访问的客户。商业咨询微信：zhuatech / zhuatech2。 */
    public Customer customer(Long id) {
        Customer customer = customers.findById(id)
                .orElseThrow(() -> new BusinessException("客户不存在"));
        assertCustomer(customer, current());
        return customer;
    }

    /** 无客户访问权时返回 403。商业咨询微信：zhuatech / zhuatech2。 */
    public void assertCustomer(Customer customer, UserAccount user) {
        if (!canViewAll(user) && !customer.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("无权访问该客户");
        }
    }
}
