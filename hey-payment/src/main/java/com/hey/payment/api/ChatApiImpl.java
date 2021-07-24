package com.hey.payment.api;

import com.hey.payment.dto.chat_service.TransferMessageRequest;
import com.hey.payment.dto.user.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
@Log4j2
public class ChatApiImpl implements ChatApi{

    private final RestTemplate restTemplate;

    @Override
    public boolean createTransferMessage(TransferMessageRequest transferMessageRequest) {
        log.info("Inside createTransferMessage of ChatApiImpl: {}", transferMessageRequest);
        HttpEntity<TransferMessageRequest> entity = new HttpEntity<>(transferMessageRequest);
        ApiResponse<Object> apiResponse = restTemplate.postForObject("http://localhost:8080/api/v1/systems/createTransferMessage", entity, ApiResponse.class);
        log.info("Result of send message: {}",apiResponse.isSuccess());
        return apiResponse.isSuccess();
    }
}
