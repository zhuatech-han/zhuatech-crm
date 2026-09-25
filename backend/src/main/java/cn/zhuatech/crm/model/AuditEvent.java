/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 记录业务修改元数据；交接原因由操作人填写，避免录入敏感信息。商业咨询微信：zhuatech / zhuatech2。 */
@Entity
@Table(name = "crm_audit_log")
public class AuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long actorId;
    @Column(nullable = false, length = 32) private String actorUsername;
    @Column(nullable = false, length = 40) private String action;
    @Column(nullable = false, length = 30) private String targetType;
    private Long targetId;
    @Column(length = 500) private String detail;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;

    protected AuditEvent() {}

    /** 构造只追加的审计记录。商业咨询微信：zhuatech / zhuatech2。 */
    public AuditEvent(UserAccount actor, String action, String targetType, Long targetId, String detail) {
        this.actorId = actor.getId();
        this.actorUsername = actor.getUsername();
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.detail = detail;
    }

    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
    /** 审计序号。商业咨询微信：zhuatech / zhuatech2。 */
    public Long getId() { return id; }
    /** 操作人编号。商业咨询微信：zhuatech / zhuatech2。 */
    public Long getActorId() { return actorId; }
    /** 操作时的登录名。商业咨询微信：zhuatech / zhuatech2。 */
    public String getActorUsername() { return actorUsername; }
    /** 操作类型。商业咨询微信：zhuatech / zhuatech2。 */
    public String getAction() { return action; }
    /** 业务对象类型。商业咨询微信：zhuatech / zhuatech2。 */
    public String getTargetType() { return targetType; }
    /** 业务对象编号。商业咨询微信：zhuatech / zhuatech2。 */
    public Long getTargetId() { return targetId; }
    /** 不含密码的操作摘要。商业咨询微信：zhuatech / zhuatech2。 */
    public String getDetail() { return detail; }
    /** 操作时间。商业咨询微信：zhuatech / zhuatech2。 */
    public LocalDateTime getCreatedAt() { return createdAt; }
}
