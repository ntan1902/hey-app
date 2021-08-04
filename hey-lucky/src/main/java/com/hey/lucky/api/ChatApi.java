package com.hey.lucky.api;

import com.hey.lucky.dto.chat_service.CheckUserInSessionChatRequest;
import com.hey.lucky.dto.chat_service.CheckUserInSessionChatResponse;
import com.hey.lucky.dto.chat_service.CreateLuckyMoneyMessageRequest;
import com.hey.lucky.dto.chat_service.CreateReceiveLuckyMoneyMessageRequest;
import com.hey.lucky.exception_handler.exception.ErrCallApiException;
import com.hey.lucky.exception_handler.exception.ErrCallChatApiException;

public interface ChatApi {
    boolean createLuckyMoneyMessage(CreateLuckyMoneyMessageRequest request) throws ErrCallChatApiException;

    boolean createReceiveLuckyMoneyMessage(CreateReceiveLuckyMoneyMessageRequest request) throws ErrCallChatApiException;

    CheckUserInSessionChatResponse checkUserInSessionChat(CheckUserInSessionChatRequest request);
}
