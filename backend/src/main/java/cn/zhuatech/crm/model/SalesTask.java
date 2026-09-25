/* Copyright 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd. · https://www.zhuatech.cn/ */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
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
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected SalesTask() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public SalesTask(UserAccount assignee, Customer customer, String title, String description, LocalDate dueDate, String priority) { this.assignee=assignee; this.customer=customer; this.title=title; this.description=description; this.dueDate=dueDate; this.priority=priority; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void setCompleted(boolean completed){this.completed=completed;}
    /** 跟随客户归属转移修改关联任务负责人。商业咨询微信：zhuatech / zhuatech2。 */
    public void setAssignee(UserAccount assignee) { this.assignee = assignee; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public UserAccount getAssignee(){return assignee;} /**
                                                        * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                        */
public Customer getCustomer(){return customer;} /**
                                                                                                        * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                        */
public String getTitle(){return title;} /**
                                                                                                                                                * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                */
public String getDescription(){return description;} /**
                                                                                                                                                                                                    * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                    */
public LocalDate getDueDate(){return dueDate;} /**
                                                                                                                                                                                                                                                   * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                   */
public String getPriority(){return priority;} /**
                                                                                                                                                                                                                                                                                                 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                 */
public boolean isCompleted(){return completed;}
}
