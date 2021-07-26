package com.hey.lucky.api;

import com.hey.lucky.dto.chat_service.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Log4j2
public class ChatApiImpl implements ChatApi {

    @Value("${CHAT_SERVICE}")
    private String CHAT_SERVICE;

    @Value("${CHAT_API_VER}")
    private String CHAT_API_VER;

    private final RestTemplate restTemplate;

    public ChatApiImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean createLuckyMoneyMessage(CreateLuckyMoneyMessageRequest request) {
        log.info("Inside createTransferMessage of ChatApiImpl: {}", request);
        HttpEntity<CreateLuckyMoneyMessageRequest> entity = new HttpEntity<>(request);
        ApiResponseChat apiResponse = restTemplate.postForObject(CHAT_SERVICE + CHAT_API_VER + "/createLuckyMoneyMessage", entity, ApiResponseChat.class);
        log.info("Result of send message: {}", apiResponse.isSuccess());
        return apiResponse.isSuccess();
    }

    @Override
    public boolean createReceiveLuckyMoneyMessage(CreateReceiveLuckyMoneyMessageRequest request) {
        log.info("Inside createTransferMessage of ChatApiImpl: {}", request);
        HttpEntity<CreateReceiveLuckyMoneyMessageRequest> entity = new HttpEntity<>(request);
        ApiResponseChat apiResponse = restTemplate.postForObject(CHAT_SERVICE + CHAT_API_VER + "/receiveLuckyMoneyMessage", entity, ApiResponseChat.class);
        log.info("Result of send message: {}", apiResponse.isSuccess());
        return apiResponse.isSuccess();
    }

    @Override
    public CheckUserInSessionChatResponse checkUserInSessionChat(CheckUserInSessionChatRequest request) {
        log.info("Check user {} is in sesstion chat {}", request.getUserId(),request.getSessionId());
        HttpEntity<CheckUserInSessionChatRequest> entity = new HttpEntity<>(request);
        CheckUserInSessionChatResponse apiResponse = restTemplate.postForObject(CHAT_SERVICE + CHAT_API_VER + "/isUserExistInSession", entity, CheckUserInSessionChatResponse.class);
        log.info("Result of call api: {}", apiResponse.isSuccess());
        return apiResponse;
    }
}
