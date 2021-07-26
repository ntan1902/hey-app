package com.hey.lucky.dto.chat_service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckUserInSessionChatRequest {
    private long userId;
    private String sessionId;
}
