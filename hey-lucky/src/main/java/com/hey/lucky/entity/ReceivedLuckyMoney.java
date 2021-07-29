package com.hey.lucky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "received_lucky_moneys")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedLuckyMoney {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "lucky_money_id")
    Long luckyMoneyId;

    @Column(name = "receiver_id")
    String receiverId;

    Long amount;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}
