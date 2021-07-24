package com.hey.payment.api;

import com.hey.payment.dto.auth_service.GetSystemInfoResponse;
import com.hey.payment.dto.auth_service.GetUserInfoResponse;
import com.hey.payment.dto.auth_service.VerifySoftTokenResponse;

public interface AuthApi {
    GetUserInfoResponse getUserInfo(long userId);
    GetSystemInfoResponse getSystemInfo(long systemId);
    VerifySoftTokenResponse verifySoftToken(String softToken);
}
