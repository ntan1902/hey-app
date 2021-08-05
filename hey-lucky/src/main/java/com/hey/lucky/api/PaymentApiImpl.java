package com.hey.lucky.api;

import com.hey.lucky.dto.payment_service.*;
import com.hey.lucky.properties.ServiceProperties;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class PaymentApiImpl implements PaymentApi {
    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    @Override
    public GetAllWalletsResponse getAllWallets() {
        return restTemplate.getForObject(serviceProperties.getPayment() + serviceProperties.getApiUrl() + "/getAllWallets", GetAllWalletsResponse.class);
    }

    @Override
    public CreateTransferFromUserResponse createTransferFromUser(TransferFromUserRequest request) {
        HttpEntity<TransferFromUserRequest> requestHttpEntity = new HttpEntity<>(request);
        return restTemplate.postForObject(serviceProperties.getPayment() + serviceProperties.getApiUrl() + "/createTransferFromUser", requestHttpEntity, CreateTransferFromUserResponse.class);
    }

    @Override
    public CreateTransferToUserResponse createTransferToUser(TransferToUserRequest request) {
        HttpEntity<TransferToUserRequest> requestHttpEntity = new HttpEntity<>(request);
        return restTemplate.postForObject(serviceProperties.getPayment() + serviceProperties.getApiUrl() + "/createTransferToUser", requestHttpEntity, CreateTransferToUserResponse.class);
    }
}
