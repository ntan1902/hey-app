package com.hey.lucky.util;

import com.hey.lucky.dto.chat_service.LuckyMoneyMessageContent;
import com.hey.lucky.dto.chat_service.ReceiveLuckyMoneyMessageContent;
import com.hey.lucky.exception_handler.exception.ErrCallChatApiException;

public interface ChatUtil {

    void sendMessageReceiveLuckyMoney(ReceiveLuckyMoneyMessageContent request) throws ErrCallChatApiException;

    void sendMessageLuckyMoney(LuckyMoneyMessageContent request) throws ErrCallChatApiException;
}
