package com.hey.lucky.dto.chat_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLuckyMoneyMessageRequest {
    private String userId;
    private String sessionId;
    private long luckyMoneyId;
    private String message;
    private String createdAt;
}
