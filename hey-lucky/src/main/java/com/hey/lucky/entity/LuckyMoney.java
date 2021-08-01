package com.hey.lucky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lucky_moneys")
public class LuckyMoney {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "system_wallet_id")
    private Long systemWalletId;

    @Column(name = "session_chat_id")
    private String sessionChatId;

    private Long amount;

    @Column(name = "rest_money")
    private Long restMoney;

    @Column(name = "number_bag")
    private int numberBag;

    @Column(name = "rest_bag")
    private int restBag;

    String type;

    @Column(name = "wish_message")
    String wishMessage;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "expired_at")
    LocalDateTime expiredAt;
}
