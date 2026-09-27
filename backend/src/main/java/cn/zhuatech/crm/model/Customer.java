/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 持久化客户资料、跟进日期、阶段及负责人。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Entity @Table(name = "crm_customer")
public class Customer extends BaseEntity {
    /**
     * 定义业务状态或固定角色取值。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public enum Status { LEAD, FOLLOWING, CUSTOMER, INACTIVE }
    @Column(nullable = false, length = 120) private String name;
    @Column(length = 60) private String shortName;
    @Column(length = 60) private String industry;
    @Column(nullable = false, length = 10) private String level = "B";
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Status status = Status.LEAD;
    @Column(length = 40) private String source;
    @Column(length = 30) private String phone;
    @Column(length = 120) private String email;
    @Column(length = 240) private String address;
    @Column(length = 1000) private String notes;
    private LocalDate nextFollowUpDate;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "owner_id") private UserAccount owner;


    protected Customer() {}

    public Customer(String name, UserAccount owner) { this.name = name; this.owner = owner; }
    /**
     * 校验请求后更新业务记录。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public void update(String name, String shortName, String industry, String level, Status status, String source, String phone, String email, String address, LocalDate nextFollowUpDate, String notes) {
        this.name=name; this.shortName=shortName; this.industry=industry; this.level=level; this.status=status; this.source=source; this.phone=phone; this.email=email; this.address=address; this.nextFollowUpDate=nextFollowUpDate; this.notes=notes;
    }
    /**
     * 设置客户下一次跟进日期。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public void setNextFollowUpDate(LocalDate nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }
    /**
     * 由授权的归属转移流程修改负责人。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public void setOwner(UserAccount owner) { this.owner = owner; }

    public String getName(){return name;}
    public String getShortName(){return shortName;}
    public String getIndustry(){return industry;}
    public String getLevel(){return level;}
    public Status getStatus(){return status;}
    public String getSource(){return source;}
    public String getPhone(){return phone;}
    public String getEmail(){return email;}
    public String getAddress(){return address;}
    public String getNotes(){return notes;}
    public LocalDate getNextFollowUpDate(){return nextFollowUpDate;}
    public UserAccount getOwner(){return owner;}
}
