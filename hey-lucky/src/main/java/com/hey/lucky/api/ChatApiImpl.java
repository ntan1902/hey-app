package com.hey.lucky.api;

import com.hey.lucky.dto.ApiResponse;
import com.hey.lucky.dto.chat_service.ApiResponseChat;
import com.hey.lucky.dto.chat_service.CreateLuckyMoneyMessageRequest;
import com.hey.lucky.dto.chat_service.CreateReceiveLuckyMoneyMessageRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
@Log4j2
public class ChatApiImpl implements ChatApi {

    @Value("${CHAT_SERVICE}")
    private static String CHAT_SERVICE;

    @Value("${CHAT_API_VER}")
    private static String CHAT_API_VER;

    private final RestTemplate restTemplate;

    @Override
    public boolean createLuckyMoneyMessage(CreateLuckyMoneyMessageRequest request) {
        log.info("Inside createTransferMessage of ChatApiImpl: {}", request);
        HttpEntity<CreateLuckyMoneyMessageRequest> entity = new HttpEntity<>(request);
        ApiResponseChat apiResponse = restTemplate.postForObject(CHAT_SERVICE + CHAT_API_VER + "/createSendLuckeyMoneyMessage", entity, ApiResponseChat.class);
        log.info("Result of send message: {}", apiResponse.isSuccess());
        return apiResponse.isSuccess();
    }

    @Override
    public boolean createReceiveLuckyMoneyMessage(CreateReceiveLuckyMoneyMessageRequest request) {
        log.info("Inside createTransferMessage of ChatApiImpl: {}", request);
        HttpEntity<CreateReceiveLuckyMoneyMessageRequest> entity = new HttpEntity<>(request);
        ApiResponseChat apiResponse = restTemplate.postForObject(CHAT_SERVICE + CHAT_API_VER + "/createRecieveLuckeyMoneyMessage", entity, ApiResponseChat.class);
        log.info("Result of send message: {}", apiResponse.isSuccess());
        return apiResponse.isSuccess();
    }
}
