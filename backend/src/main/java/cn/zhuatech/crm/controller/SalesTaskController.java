/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.controller;

import cn.zhuatech.crm.common.*;
import cn.zhuatech.crm.dto.CrmDto.*;
import cn.zhuatech.crm.model.SalesTask;
import cn.zhuatech.crm.repository.SalesTaskRepository;
import cn.zhuatech.crm.service.CrmAccessService;
import cn.zhuatech.crm.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 提供销售任务查询、创建、完成和删除接口。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@RestController @RequestMapping("/api/tasks")
public class SalesTaskController {
    private final SalesTaskRepository tasks; private final CrmAccessService access; private final AuditService audit;

    public SalesTaskController(SalesTaskRepository tasks,CrmAccessService access,AuditService audit){this.tasks=tasks;this.access=access;this.audit=audit;}
    /**
     * 列出符合访问范围及筛选条件的业务记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @GetMapping public ApiResponse<List<TaskView>> list(){return ApiResponse.ok(tasks.findByAssigneeOrderByCompletedAscDueDateAsc(access.current()).stream().map(TaskView::from).toList());}
    /**
     * 校验业务请求并持久化新增记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PostMapping @Transactional public ApiResponse<TaskView> create(@Valid @RequestBody TaskRequest r){var customer=r.customerId()==null?null:access.customer(r.customerId());var task=new SalesTask(access.current(),customer,r.title(),r.description(),r.dueDate(),r.priority()==null?"MEDIUM":r.priority());tasks.save(task);audit.record("TASK_CREATE","TASK",task.getId(),null);return ApiResponse.ok("销售任务已创建",TaskView.from(task));}
    /**
     * 校验任务访问权限并调整完成状态。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @PatchMapping("/{id}") @Transactional public ApiResponse<TaskView> status(@PathVariable Long id,@RequestBody TaskStatusRequest r){SalesTask task=tasks.findByIdAndAssignee(id,access.current()).orElseThrow(()->new BusinessException("任务不存在或无权操作"));task.setCompleted(r.completed());audit.record("TASK_STATUS","TASK",id,r.completed()?"已完成":"未完成");return ApiResponse.ok(TaskView.from(task));}
    /**
     * 校验权限与关联约束后删除业务记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    @DeleteMapping("/{id}") @Transactional public ApiResponse<Void> delete(@PathVariable Long id){SalesTask task=tasks.findByIdAndAssignee(id,access.current()).orElseThrow(()->new BusinessException("任务不存在或无权操作"));tasks.delete(task);audit.record("TASK_DELETE","TASK",id,null);return ApiResponse.ok("任务已删除",null);}
}
