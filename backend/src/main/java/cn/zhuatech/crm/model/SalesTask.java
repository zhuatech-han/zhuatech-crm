/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 持久化销售待办、执行人及完成状态。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Entity @Table(name = "crm_sales_task")
public class SalesTask extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "assignee_id") private UserAccount assignee;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "customer_id") private Customer customer;
    @Column(nullable = false, length = 120) private String title;
    @Column(length = 500) private String description;
    private LocalDate dueDate;
    @Column(nullable = false, length = 20) private String priority;
    @Column(nullable = false) private boolean completed;

    protected SalesTask() {}

    public SalesTask(UserAccount assignee, Customer customer, String title, String description, LocalDate dueDate, String priority) { this.assignee=assignee; this.customer=customer; this.title=title; this.description=description; this.dueDate=dueDate; this.priority=priority; }
    /**
     * 修改任务完成标记。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public void setCompleted(boolean completed){this.completed=completed;}
    /**
     * 跟随客户归属转移修改关联任务负责人。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public void setAssignee(UserAccount assignee) { this.assignee = assignee; }

    public UserAccount getAssignee(){return assignee;}
    public Customer getCustomer(){return customer;}
    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public LocalDate getDueDate(){return dueDate;}
    public String getPriority(){return priority;}
    public boolean isCompleted(){return completed;}
}
