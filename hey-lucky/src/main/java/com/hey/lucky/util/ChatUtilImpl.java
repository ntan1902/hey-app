package com.hey.lucky.util;

import com.hey.lucky.api.ChatApi;
import com.hey.lucky.dto.chat_service.LuckyMoneyMessageContent;
import com.hey.lucky.dto.chat_service.ReceiveLuckyMoneyMessageContent;
import com.hey.lucky.exception_handler.exception.ErrCallChatApiException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ChatUtilImpl implements ChatUtil{
    private final ChatApi chatApi;

    public ChatUtilImpl(ChatApi chatApi) {
        this.chatApi = chatApi;
    }

    @Override
    public void sendMessageReceiveLuckyMoney(ReceiveLuckyMoneyMessageContent request) throws ErrCallChatApiException {
        log.info("Send message receive lucky money");
        chatApi.createReceiveLuckyMoneyMessage(request);
    }

    @Override
    public void sendMessageLuckyMoney(LuckyMoneyMessageContent request) throws ErrCallChatApiException {
        log.info("Send message lucky money");
        chatApi.createLuckyMoneyMessage(request);
    }
}
