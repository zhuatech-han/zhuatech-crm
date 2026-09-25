/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.service;

import cn.zhuatech.crm.model.AuditEvent;
import cn.zhuatech.crm.repository.AuditEventRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

/** 将业务修改与操作者一起记录；摘要不主动写入密码或客户联系方式。商业咨询微信：zhuatech / zhuatech2。 */
@Service
public class AuditService {
    private final AuditEventRepository events;
    private final CurrentUserService current;

    public AuditService(AuditEventRepository events, CurrentUserService current) {
        this.events = events;
        this.current = current;
    }

    /** 在调用方事务内写入审计事件。商业咨询微信：zhuatech / zhuatech2。 */
    public void record(String action, String targetType, Long targetId, String detail) {
        events.save(new AuditEvent(current.get(), action, targetType, targetId, detail));
    }

    /** 管理员查看最近 100 条记录。商业咨询微信：zhuatech / zhuatech2。 */
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditEvent> recent() { return events.findTop100ByOrderByIdDesc(); }
}
