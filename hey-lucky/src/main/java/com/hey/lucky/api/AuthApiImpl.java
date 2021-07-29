package com.hey.lucky.api;

import com.hey.lucky.dto.auth_service.GetUserInfoResponse;
import lombok.AllArgsConstructor;
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
}
