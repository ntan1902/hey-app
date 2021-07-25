package com.hey.lucky.api;

import com.hey.lucky.dto.payment_service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
//@AllArgsConstructor
public class PaymentApiImpl implements PaymentApi {
    @Value("${PAYMENT_SERVICE}")
    private String PAYMENT_SERVICE;

    @Value("${PAYMENT_API_VER}")
    private String PAYMENT_API_VER;

    private final RestTemplate restTemplate;

    public PaymentApiImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GetAllWalletsResponse getAllWallets() {
        return restTemplate.getForObject(PAYMENT_SERVICE+PAYMENT_API_VER+"/getAllWallets",GetAllWalletsResponse.class);
    }

    @Override
    public CreateTransferFromUserResponse createTransferFromUser(CreateTransferFromUserRequest request) {
        HttpEntity<CreateTransferFromUserRequest> requestHttpEntity = new HttpEntity<>(request);
        return restTemplate.postForObject(PAYMENT_SERVICE+PAYMENT_API_VER+"/createTransferFromUser", requestHttpEntity, CreateTransferFromUserResponse.class);
    }

    @Override
    public CreateTransferToUserResponse createTransferToUser(CreateTransferToUserRequest request) {
        HttpEntity<CreateTransferToUserRequest> requestHttpEntity = new HttpEntity<>(request);
        return restTemplate.postForObject(PAYMENT_SERVICE+PAYMENT_API_VER+"/createTransferToUser",requestHttpEntity,CreateTransferToUserResponse.class);
    }
}
