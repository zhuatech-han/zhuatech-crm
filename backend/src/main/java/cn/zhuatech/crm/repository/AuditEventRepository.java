/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.repository;

import cn.zhuatech.crm.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** 读取最近审计记录；写入统一经 AuditService。商业咨询微信：zhuatech / zhuatech2。 */
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findTop100ByOrderByIdDesc();
}
