/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.ApiResponse;
import cn.zhuatech.crm.model.AuditEvent;
import cn.zhuatech.crm.service.AuditService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/** 管理员审计记录查询。商业咨询微信：zhuatech / zhuatech2。 */
@RestController @RequestMapping("/api/audit-events")
public class AuditController {
    private final AuditService audit;

    public AuditController(AuditService audit) { this.audit = audit; }

    /** 返回最近 100 条业务操作，不支持从接口删除或修改。商业咨询微信：zhuatech / zhuatech2。 */
    @GetMapping public ApiResponse<List<AuditEvent>> recent() { return ApiResponse.ok(audit.recent()); }
}
