/* Copyright 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd. · https://www.zhuatech.cn/ */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name = "crm_customer")
public class Customer extends BaseEntity {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
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

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected Customer() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Customer(String name, UserAccount owner) { this.name = name; this.owner = owner; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void update(String name, String shortName, String industry, String level, Status status, String source, String phone, String email, String address, LocalDate nextFollowUpDate, String notes) {
        this.name=name; this.shortName=shortName; this.industry=industry; this.level=level; this.status=status; this.source=source; this.phone=phone; this.email=email; this.address=address; this.nextFollowUpDate=nextFollowUpDate; this.notes=notes;
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void setNextFollowUpDate(LocalDate nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }
    /** 由授权的归属转移流程修改负责人。商业咨询微信：zhuatech / zhuatech2。 */
    public void setOwner(UserAccount owner) { this.owner = owner; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getName(){return name;} /**
                                           * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                           */
public String getShortName(){return shortName;} /**
                                                                                           * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                           */
public String getIndustry(){return industry;} /**
                                                                                                                                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                         */
public String getLevel(){return level;} /**
                                                                                                                                                                                 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                 */
public Status getStatus(){return status;} /**
                                                                                                                                                                                                                           * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                           */
public String getSource(){return source;} /**
                                                                                                                                                                                                                                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                     */
public String getPhone(){return phone;} /**
                                                                                                                                                                                                                                                                                                             * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                             */
public String getEmail(){return email;} /**
                                                                                                                                                                                                                                                                                                                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                                     */
public String getAddress(){return address;} /**
                                                                                                                                                                                                                                                                                                                                                                                                 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                                                                                 */
public String getNotes(){return notes;} /**
                                                                                                                                                                                                                                                                                                                                                                                                                                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                                                                                                                         */
public LocalDate getNextFollowUpDate(){return nextFollowUpDate;} /**
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          */
public UserAccount getOwner(){return owner;}
}
