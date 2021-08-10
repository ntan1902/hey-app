package com.hey.lucky.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LuckyMoneyDTO {
    private String senderName;
    private long luckyMoneyId;
    private boolean isReceived;
    private long receivedMoney;
    private String receivedAt;
    private String wishMessage;
    private int restBag;
    private int totalBag;
    private int totalMoney;
    private boolean isExpired;
}
