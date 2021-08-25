package com.hey.integration.entity;

import javax.persistence.*;

@Entity
@Table(
        name = "wallets",
        indexes = {
                @Index(name = "i_owner_id", columnList = "owner_id"),
        }
)
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long balance;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "ref_from")
    private String refFrom;

    public Wallet(Long id, Long balance, String ownerId, String refFrom) {
        this.id = id;
        this.balance = balance;
        this.ownerId = ownerId;
        this.refFrom = refFrom;
    }

    public Wallet() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRefFrom() {
        return refFrom;
    }

    public void setRefFrom(String refFrom) {
        this.refFrom = refFrom;
    }
}
