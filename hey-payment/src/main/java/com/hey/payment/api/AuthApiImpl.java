package com.hey.payment.api;

import com.hey.payment.dto.auth_service.SoftTokenEncoded;
import com.hey.payment.dto.auth_service.SystemInfo;
import com.hey.payment.dto.auth_service.UserInfo;
import com.hey.payment.dto.auth_service.VerifySoftTokenRequest;
import com.hey.payment.dto.user.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class AuthApiImpl implements AuthApi {
    private final RestTemplate restTemplate;

    @Override
    public ApiResponse<UserInfo> getUserInfo(long userId) {
        return restTemplate.getForObject("/getUserInfo/" + userId, ApiResponse.class);
    }

    @Override
    public ApiResponse<SystemInfo> getSystemInfo(long systemId) {
        return restTemplate.getForObject("/getSystemInfo/" + systemId, ApiResponse.class);
    }

    @Override
    public ApiResponse<SoftTokenEncoded> verifySoftToken(String softToken) {
        HttpEntity<VerifySoftTokenRequest> entity = new HttpEntity<>(new VerifySoftTokenRequest(softToken));
        return restTemplate.postForObject("/authorizeSoftToken", entity, ApiResponse.class);
    }
}
