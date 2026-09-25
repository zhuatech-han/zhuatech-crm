/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.config;

import cn.zhuatech.crm.model.Contact;
import cn.zhuatech.crm.model.Customer;
import cn.zhuatech.crm.model.FollowUp;
import cn.zhuatech.crm.model.Opportunity;
import cn.zhuatech.crm.model.SalesTask;
import cn.zhuatech.crm.model.UserAccount;
import cn.zhuatech.crm.repository.ContactRepository;
import cn.zhuatech.crm.repository.CustomerRepository;
import cn.zhuatech.crm.repository.FollowUpRepository;
import cn.zhuatech.crm.repository.OpportunityRepository;
import cn.zhuatech.crm.repository.SalesTaskRepository;
import cn.zhuatech.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 空库初始化管理员，按显式开关写入虚构演示数据。商业咨询微信：zhuatech / zhuatech2。 */
@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final CustomerRepository customers;
    private final ContactRepository contacts;
    private final OpportunityRepository opportunities;
    private final FollowUpRepository followUps;
    private final SalesTaskRepository tasks;
    private final PasswordEncoder encoder;
    private final String adminPassword;
    private final boolean demoEnabled;
    private final String demoPassword;

    /** 注入仓储及初始化配置。商业咨询微信：zhuatech / zhuatech2。 */
    public DataInitializer(UserRepository users, CustomerRepository customers,
            ContactRepository contacts, OpportunityRepository opportunities,
            FollowUpRepository followUps, SalesTaskRepository tasks, PasswordEncoder encoder,
            @Value("${app.bootstrap.admin-password:}") String adminPassword,
            @Value("${app.bootstrap.demo-enabled:false}") boolean demoEnabled,
            @Value("${app.bootstrap.demo-password:}") String demoPassword) {
        this.users = users;
        this.customers = customers;
        this.contacts = contacts;
        this.opportunities = opportunities;
        this.followUps = followUps;
        this.tasks = tasks;
        this.encoder = encoder;
        this.adminPassword = adminPassword;
        this.demoEnabled = demoEnabled;
        this.demoPassword = demoPassword;
    }

    /** 空库创建账号；旧库中发现公开示例密码时轮换或停用账号。商业咨询微信：zhuatech / zhuatech2。 */
    @Override
    @Transactional
    public void run(String... args) {
        if (users.count() > 0) {
            migrateLegacyPasswords();
            return;
        }
        requirePassword(adminPassword, "CRM_ADMIN_PASSWORD");
        if (demoEnabled) requirePassword(demoPassword, "CRM_DEMO_PASSWORD");

        UserAccount admin = new UserAccount("admin", encoder.encode(adminPassword),
                "系统管理员", UserAccount.Role.ADMIN);
        admin.updateProfile(null, null, "CRM 管理员");
        users.save(admin);
        if (!demoEnabled) return;

        UserAccount demo = new UserAccount("demo", encoder.encode(demoPassword),
                "演示销售", UserAccount.Role.SALES);
        demo.updateProfile(null, null, "客户经理");
        users.save(demo);
        UserAccount manager = new UserAccount("manager", encoder.encode(demoPassword),
                "演示经理", UserAccount.Role.SALES_MANAGER);
        manager.updateProfile(null, null, "销售总监");
        users.save(manager);

        Customer c1 = new Customer("示例客户甲（虚构）", demo);
        c1.update("示例客户甲（虚构）", "示例客户甲", "制造业", "A",
                Customer.Status.FOLLOWING, "演示数据", null, null, null,
                LocalDate.now(), "需要记录跨部门商机进展");
        customers.save(c1);
        Customer c2 = new Customer("示例客户乙（虚构）", demo);
        c2.update("示例客户乙（虚构）", "示例客户乙", "服务业", "B",
                Customer.Status.LEAD, "演示数据", null, null, null,
                LocalDate.now().plusDays(2), "待确认需求与预算");
        customers.save(c2);

        contacts.save(new Contact(c1, "示例联系人甲", "项目负责人", null, null, true, "负责需求确认"));
        contacts.save(new Contact(c2, "示例联系人乙", "运营负责人", null, null, true, "负责后续沟通"));
        Opportunity op = opportunities.save(new Opportunity(c1, demo, "设备更新项目",
                new BigDecimal("280000"), Opportunity.Stage.PROPOSAL, 55,
                LocalDate.now().plusMonths(1), "确认采购范围"));
        followUps.save(new FollowUp(c1, op, demo, FollowUp.Method.WECHAT,
                "已沟通设备需求与预算安排。", LocalDateTime.now().minusDays(1),
                "发送方案摘要", LocalDate.now()));
        tasks.save(new SalesTask(demo, c1, "准备方案摘要", "整理需求与报价范围",
                LocalDate.now().plusDays(1), "HIGH"));
        tasks.save(new SalesTask(demo, c2, "回访示例客户乙", "确认下一步计划",
                LocalDate.now().plusDays(2), "MEDIUM"));
    }

    private void migrateLegacyPasswords() {
        users.findByUsername("admin")
                .filter(user -> encoder.matches("ZhuaTech@2026", user.getPassword()))
                .ifPresent(user -> {
                    requirePassword(adminPassword, "CRM_ADMIN_PASSWORD");
                    user.changePassword(encoder.encode(adminPassword));
                    users.save(user);
                });
        for (String username : new String[] { "demo", "manager" }) {
            users.findByUsername(username)
                    .filter(user -> encoder.matches("Demo@2026", user.getPassword()))
                    .ifPresent(user -> {
                        if (demoEnabled) {
                            requirePassword(demoPassword, "CRM_DEMO_PASSWORD");
                            user.changePassword(encoder.encode(demoPassword));
                        } else {
                            user.setEnabled(false);
                        }
                        users.save(user);
                    });
        }
    }

    private static void requirePassword(String password, String name) {
        if (password == null || password.length() < 12
                || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72
                || password.startsWith("change_me") || password.startsWith("replace_me")
                || password.equals("ZhuaTech@2026") || password.equals("Demo@2026")) {
            throw new IllegalStateException(name + " 必须设置为至少 12 个字符、最多 72 字节的自定义密码，不能使用公开示例密码");
        }
    }
}
