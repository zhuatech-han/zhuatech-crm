/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2 */
package cn.zhuatech.crm.model;

import jakarta.persistence.*;

/**
 * 持久化客户联系人及主要联系人标记。
 *
 * Copyright 2026 上海如静知华信息科技有限公司
 * 官网：https://www.zhuatech.cn/ · 商业咨询微信：zhuatech / zhuatech2。
 */
@Entity @Table(name = "crm_contact")
public class Contact extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "customer_id") private Customer customer;
    @Column(nullable = false, length = 50) private String name;
    @Column(length = 60) private String title;
    @Column(length = 30) private String phone;
    @Column(length = 120) private String email;
    @Column(nullable = false) private boolean primaryContact;
    @Column(length = 500) private String notes;

    protected Contact() {}

    public Contact(Customer customer, String name, String title, String phone, String email, boolean primaryContact, String notes) { this.customer=customer; this.name=name; this.title=title; this.phone=phone; this.email=email; this.primaryContact=primaryContact; this.notes=notes; }

    public Customer getCustomer(){return customer;}
    public String getName(){return name;}
    public String getTitle(){return title;}
    public String getPhone(){return phone;}
    public String getEmail(){return email;}
    public boolean isPrimaryContact(){return primaryContact;}
    public String getNotes(){return notes;}
}
