/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.config;

import cn.zhuatech.crm.repository.*;
import cn.zhuatech.crm.model.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 验证空库初始化不会创建固定密码账号或意外演示数据。
 * 商业授权咨询微信：zhuatech / zhuatech2。
 */
class DataInitializerTests {
    private final UserRepository users = mock(UserRepository.class);
    private final CustomerRepository customers = mock(CustomerRepository.class);
    private final ContactRepository contacts = mock(ContactRepository.class);
    private final OpportunityRepository opportunities = mock(OpportunityRepository.class);
    private final FollowUpRepository followUps = mock(FollowUpRepository.class);
    private final SalesTaskRepository tasks = mock(SalesTaskRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);

    private DataInitializer initializer(String adminPassword, boolean demoEnabled, String demoPassword) {
        return new DataInitializer(users, customers, contacts, opportunities, followUps, tasks,
                encoder, adminPassword, demoEnabled, demoPassword);
    }

    @Test void emptyDatabaseRejectsMissingAdminPassword() {
        assertThrows(IllegalStateException.class, () -> initializer("", false, "").run());
        verify(users, never()).save(any());
    }

    @Test void demoModeRejectsMissingDemoPasswordBeforeWritingUsers() {
        assertThrows(IllegalStateException.class,
                () -> initializer("long-admin-password-for-test", true, "").run());
        verify(users, never()).save(any());
    }

    @Test void nonDemoBootstrapCreatesOnlyAdmin() {
        initializer("long-admin-password-for-test", false, "").run();
        verify(users, times(1)).save(any());
        verifyNoInteractions(customers, contacts, opportunities, followUps, tasks);
    }

    @Test void existingDatabaseRotatesPublishedAdminPassword() {
        UserAccount admin = new UserAccount("admin", "old-hash", "管理员", UserAccount.Role.ADMIN);
        when(users.count()).thenReturn(1L);
        when(users.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(encoder.matches("ZhuaTech@2026", "old-hash")).thenReturn(true);
        when(encoder.encode("long-admin-password-for-test")).thenReturn("new-hash");

        initializer("long-admin-password-for-test", false, "").run();

        assertEquals("new-hash", admin.getPassword());
        assertEquals(1, admin.getTokenVersion());
        verify(users).save(admin);
    }

    @Test void existingDatabaseDisablesLegacyDemoWithoutDeletingItsData() {
        UserAccount demo = new UserAccount("demo", "old-demo-hash", "演示销售", UserAccount.Role.SALES);
        when(users.count()).thenReturn(1L);
        when(users.findByUsername("demo")).thenReturn(Optional.of(demo));
        when(encoder.matches("Demo@2026", "old-demo-hash")).thenReturn(true);

        initializer("", false, "").run();

        assertFalse(demo.isEnabled());
        assertEquals(1, demo.getTokenVersion());
        verify(users).save(demo);
        verifyNoInteractions(customers, contacts, opportunities, followUps, tasks);
    }
}
