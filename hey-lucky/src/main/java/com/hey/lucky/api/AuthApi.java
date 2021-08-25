package com.hey.lucky.api;

import com.hey.lucky.dto.auth_service.GetUserInfoResponse;
import com.hey.lucky.dto.auth_service.VerifySoftTokenResponse;

public interface AuthApi {
    GetUserInfoResponse getUserInfo(String userId);
    VerifySoftTokenResponse verifySoftToken(String softToken);
}
