/* Copyright 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd. · https://www.zhuatech.cn/ */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name = "crm_opportunity")
public class Opportunity extends BaseEntity {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum Stage { LEAD, DISCOVERY, PROPOSAL, NEGOTIATION, WON, LOST }
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "customer_id") private Customer customer;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "owner_id") private UserAccount owner;
    @Column(nullable = false, length = 120) private String name;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal amount = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Stage stage = Stage.LEAD;
    @Column(nullable = false) private Integer probability = 10;
    private LocalDate expectedCloseDate;
    @Column(length = 500) private String nextStep;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected Opportunity() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Opportunity(Customer customer, UserAccount owner, String name, BigDecimal amount, Stage stage, int probability, LocalDate expectedCloseDate, String nextStep) { this.customer=customer; this.owner=owner; this.name=name; this.amount=amount; this.stage=stage; this.probability=probability; this.expectedCloseDate=expectedCloseDate; this.nextStep=nextStep; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void changeStage(Stage stage, int probability, String nextStep) { this.stage=stage; this.probability=probability; this.nextStep=nextStep; }
    /** 跟随客户归属转移修改商机负责人。商业咨询微信：zhuatech / zhuatech2。 */
    public void setOwner(UserAccount owner) { this.owner = owner; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Customer getCustomer(){return customer;} /**
                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                     */
public UserAccount getOwner(){return owner;} /**
                                                                                                  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                  */
public String getName(){return name;} /**
                                                                                                                                        * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                        */
public BigDecimal getAmount(){return amount;} /**
                                                                                                                                                                                      * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                      */
public Stage getStage(){return stage;} /**
                                                                                                                                                                                                                             * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                             */
public Integer getProbability(){return probability;} /**
                                                                                                                                                                                                                                                                                  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                  */
public LocalDate getExpectedCloseDate(){return expectedCloseDate;} /**
                                                                                                                                                                                                                                                                                                                                                     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                                                                                                     */
public String getNextStep(){return nextStep;}
}
