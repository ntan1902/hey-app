package com.hey.payment.api;

import com.hey.payment.dto.chat_system.TransferMessageRequest;
import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.properties.ServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Log4j2
@AllArgsConstructor
public class ChatApiImpl implements ChatApi {
    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;


    @Override
    public boolean createTransferMessage(TransferMessageRequest transferMessageRequest) {
        log.info("Inside createTransferMessage of ChatApiImpl: {}", transferMessageRequest);
        HttpEntity<TransferMessageRequest> entity = new HttpEntity<>(transferMessageRequest);
        ApiResponse<Object> apiResponse = restTemplate.postForObject(
                serviceProperties.getChat() + serviceProperties.getApiUrl() + "/createTransferMessage",
                entity,
                ApiResponse.class
        );
        log.info("Result of send message: {}", apiResponse.isSuccess());
        return apiResponse.isSuccess();
    }
}
