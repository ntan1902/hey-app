package com.hey.lucky.api;

import com.hey.lucky.dto.chat_service.CreateLuckyMoneyMessageRequest;
import com.hey.lucky.dto.chat_service.CreateReceiveLuckyMoneyMessageRequest;

public interface ChatApi {
    boolean createLuckyMoneyMessage(CreateLuckyMoneyMessageRequest request);
    boolean createReceiveLuckyMoneyMessage(CreateReceiveLuckyMoneyMessageRequest request);
}
