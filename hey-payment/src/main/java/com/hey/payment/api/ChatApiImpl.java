package com.hey.payment.api;

import com.hey.payment.dto.chat_service.TransferMessageRequest;
import com.hey.payment.dto.user.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class ChatApiImpl implements ChatApi{

    private final RestTemplate restTemplate;

    @Override
    public boolean createTransferMessage(TransferMessageRequest transferMessageRequest) {
        HttpEntity<TransferMessageRequest> entity = new HttpEntity<>(transferMessageRequest);
        ApiResponse<Object> apiResponse = restTemplate.postForObject("/createTransferMessage", entity, ApiResponse.class);
        return apiResponse.isSuccess();
    }
}
