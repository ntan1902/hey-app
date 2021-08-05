package com.hey.lucky.api;

import com.hey.lucky.dto.chat_service.CheckUserInSessionChatRequest;
import com.hey.lucky.dto.chat_service.CheckUserInSessionChatResponse;
import com.hey.lucky.dto.chat_service.LuckyMoneyMessageContent;
import com.hey.lucky.dto.chat_service.ReceiveLuckyMoneyMessageContent;
import com.hey.lucky.exception_handler.exception.ErrCallChatApiException;

public interface ChatApi {
    boolean createLuckyMoneyMessage(LuckyMoneyMessageContent request) throws ErrCallChatApiException;

    boolean createReceiveLuckyMoneyMessage(ReceiveLuckyMoneyMessageContent request) throws ErrCallChatApiException;

    CheckUserInSessionChatResponse checkUserInSessionChat(CheckUserInSessionChatRequest request);
}
