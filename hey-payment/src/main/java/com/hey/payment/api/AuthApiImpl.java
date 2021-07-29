package com.hey.payment.api;

import com.hey.payment.dto.auth_system.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class AuthApiImpl implements AuthApi {
    private final RestTemplate restTemplate;

    @Override
    public GetUserInfoResponse getUserInfo(String userId) {
        return restTemplate.getForObject("/getUserInfo/" + userId, GetUserInfoResponse.class);
    }

    @Override
    public GetSystemInfoResponse getSystemInfo(String systemId) {
        return restTemplate.getForObject("/getSystemInfo/" + systemId, GetSystemInfoResponse.class);
    }

    @Override
    public VerifySoftTokenResponse verifySoftToken(String softToken) {
        HttpEntity<VerifySoftTokenRequest> entity = new HttpEntity<>(new VerifySoftTokenRequest(softToken));
        return restTemplate.postForObject("/authorizeSoftToken", entity, VerifySoftTokenResponse.class);
    }
}
