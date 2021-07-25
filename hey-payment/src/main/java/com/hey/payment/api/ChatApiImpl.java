package com.hey.payment.api;

import com.hey.payment.dto.chat_system.TransferMessageRequest;
import com.hey.payment.dto.user.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Log4j2
public class ChatApiImpl implements ChatApi{

    private final RestTemplate restTemplate;

    @Value("${CHAT_SYSTEM}")
    private String CHAT_SYSTEM;

    @Autowired
    public ChatApiImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean createTransferMessage(TransferMessageRequest transferMessageRequest) {
        log.info("Inside createTransferMessage of ChatApiImpl: {}", transferMessageRequest);
        HttpEntity<TransferMessageRequest> entity = new HttpEntity<>(transferMessageRequest);
        ApiResponse<Object> apiResponse = restTemplate.postForObject(CHAT_SYSTEM + "/api/v1/systems/createTransferMessage", entity, ApiResponse.class);
        log.info("Result of send message: {}",apiResponse.isSuccess());
        return apiResponse.isSuccess();
    }
}
