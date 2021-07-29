package com.hey.lucky.dto.chat_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateReceiveLuckyMoneyMessageRequest {
    private String sessionId;
    private String receiverId;
    private long luckyMoneyId;
    private long amount;
    private String message;
    private String createdAt;
}
