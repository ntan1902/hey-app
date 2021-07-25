package com.hey.lucky.api;

import com.hey.lucky.dto.chat_service.CreateLuckyMoneyMessageRequest;

public interface ChatApi {
    boolean createLuckyMoneyMessage(CreateLuckyMoneyMessageRequest request);
}
