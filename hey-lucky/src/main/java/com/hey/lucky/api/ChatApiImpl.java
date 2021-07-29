package com.hey.lucky.api;

import com.hey.lucky.dto.chat_service.*;
import com.hey.lucky.exception_handler.exception.ErrCallApiException;
import com.hey.lucky.properties.ServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Log4j2
@AllArgsConstructor
public class ChatApiImpl implements ChatApi {
    private final ServiceProperties serviceProperties;

    private final RestTemplate restTemplate;


    @Override
    public boolean createLuckyMoneyMessage(CreateLuckyMoneyMessageRequest request) {
        log.info("Inside createTransferMessage of ChatApiImpl: {}", request);
        HttpEntity<CreateLuckyMoneyMessageRequest> entity = new HttpEntity<>(request);
        ResponseEntity<ApiResponseChat> apiResponseEntity = restTemplate.postForEntity(serviceProperties.getChat() + serviceProperties.getApiUrl() + "/createLuckyMoneyMessage", entity, ApiResponseChat.class);
        if (!apiResponseEntity.getStatusCode().equals(HttpStatus.OK)){
            throw new ErrCallApiException("Can not send message");
        }
        ApiResponseChat apiResponse = apiResponseEntity.getBody();
        log.info("Result of send message: {}", apiResponse.isSuccess());
        return apiResponse.isSuccess();
    }

    @Override
    public boolean createReceiveLuckyMoneyMessage(CreateReceiveLuckyMoneyMessageRequest request) {
        log.info("Inside createTransferMessage of ChatApiImpl: {}", request);
        HttpEntity<CreateReceiveLuckyMoneyMessageRequest> entity = new HttpEntity<>(request);
        ResponseEntity<ApiResponseChat> apiResponseEntity = restTemplate.postForEntity(serviceProperties.getChat() + serviceProperties.getApiUrl() + "/receiveLuckyMoneyMessage", entity, ApiResponseChat.class);
        if (!apiResponseEntity.getStatusCode().equals(HttpStatus.OK)){
            throw new ErrCallApiException("Can not send message");
        }
        ApiResponseChat apiResponse = apiResponseEntity.getBody();
        log.info("Result of send message: {}", apiResponse.isSuccess());
        return apiResponse.isSuccess();
    }

    @Override
    public CheckUserInSessionChatResponse checkUserInSessionChat(CheckUserInSessionChatRequest request) {
        log.info("Check user {} is in sesstion chat {}", request.getUserId(),request.getSessionId());
        HttpEntity<CheckUserInSessionChatRequest> entity = new HttpEntity<>(request);
        CheckUserInSessionChatResponse apiResponse = restTemplate.postForObject(serviceProperties.getChat() + serviceProperties.getApiUrl() + "/isUserExistInSession", entity, CheckUserInSessionChatResponse.class);
        log.info("Result of call api: {}", apiResponse.isSuccess());
        return apiResponse;
    }
}
