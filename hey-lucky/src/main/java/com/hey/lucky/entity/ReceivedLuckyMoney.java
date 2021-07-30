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
@Table(
        name = "received_lucky_moneys",
        indexes = {
                @Index(name = "i_receiver_id", columnList = "receiver_id"),
                @Index(name = "i_lucky_money_id", columnList = "lucky_money_id"),
        }
)
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
