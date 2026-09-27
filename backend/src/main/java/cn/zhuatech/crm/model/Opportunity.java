/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 持久化客户商机、金额、阶段及预计成交信息。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Entity @Table(name = "crm_opportunity")
public class Opportunity extends BaseEntity {
    /**
     * 定义业务状态或固定角色取值。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
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

    protected Opportunity() {}

    public Opportunity(Customer customer, UserAccount owner, String name, BigDecimal amount, Stage stage, int probability, LocalDate expectedCloseDate, String nextStep) { this.customer=customer; this.owner=owner; this.name=name; this.amount=amount; this.stage=stage; this.probability=probability; this.expectedCloseDate=expectedCloseDate; this.nextStep=nextStep; }
    /**
     * 调整商机阶段及对应成交概率。
     *
     * Copyright 2026 上海如静知华信息科技有限公司
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public void changeStage(Stage stage, int probability, String nextStep) { this.stage=stage; this.probability=probability; this.nextStep=nextStep; }
    /**
     * 跟随客户归属转移修改商机负责人。商业咨询微信：zhuatech / zhuatech2。
     * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
     */
    public void setOwner(UserAccount owner) { this.owner = owner; }

    public Customer getCustomer(){return customer;}
    public UserAccount getOwner(){return owner;}
    public String getName(){return name;}
    public BigDecimal getAmount(){return amount;}
    public Stage getStage(){return stage;}
    public Integer getProbability(){return probability;}
    public LocalDate getExpectedCloseDate(){return expectedCloseDate;}
    public String getNextStep(){return nextStep;}
}
