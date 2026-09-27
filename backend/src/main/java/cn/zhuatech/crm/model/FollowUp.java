/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;
import java.time.*;

/**
 * 持久化销售跟进内容、关联客户与下一步计划。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Entity @Table(name = "crm_follow_up")
public class FollowUp extends BaseEntity {
    /**
     * 定义业务状态或固定角色取值。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public enum Method { PHONE, WECHAT, VISIT, EMAIL, OTHER }
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "customer_id") private Customer customer;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "opportunity_id") private Opportunity opportunity;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "creator_id") private UserAccount creator;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Method method;
    @Column(nullable = false, length = 1000) private String content;
    @Column(nullable = false) private LocalDateTime followUpAt;
    @Column(length = 500) private String nextAction;
    private LocalDate nextFollowUpDate;

    protected FollowUp() {}

    public FollowUp(Customer customer, Opportunity opportunity, UserAccount creator, Method method, String content, LocalDateTime followUpAt, String nextAction, LocalDate nextFollowUpDate) { this.customer=customer; this.opportunity=opportunity; this.creator=creator; this.method=method; this.content=content; this.followUpAt=followUpAt; this.nextAction=nextAction; this.nextFollowUpDate=nextFollowUpDate; }

    public Customer getCustomer(){return customer;}
    public Opportunity getOpportunity(){return opportunity;}
    public UserAccount getCreator(){return creator;}
    public Method getMethod(){return method;}
    public String getContent(){return content;}
    public LocalDateTime getFollowUpAt(){return followUpAt;}
    public String getNextAction(){return nextAction;}
    public LocalDate getNextFollowUpDate(){return nextFollowUpDate;}
}
