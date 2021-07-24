package com.hey.lucky.entity;

import javax.persistence.*;

@Entity
@Table(name = "lucky_moneys")
public class LuckyMoney {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "system_wallet_id")
    private Long systemWalletId;

    private Long amount;

    @Column(name = "rest_money")
    private Long restMoney;

    @Column(name = "number_bag")
    private int numberBag;

    @Column(name = "rest_bag")
    private int restBag;
}
