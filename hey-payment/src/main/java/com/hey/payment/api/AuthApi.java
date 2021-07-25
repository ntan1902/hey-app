package com.hey.payment.api;

import com.hey.payment.dto.auth_system.GetSystemInfoResponse;
import com.hey.payment.dto.auth_system.GetUserInfoResponse;
import com.hey.payment.dto.auth_system.VerifySoftTokenResponse;

public interface AuthApi {
    GetUserInfoResponse getUserInfo(long userId);
    GetSystemInfoResponse getSystemInfo(long systemId);
    VerifySoftTokenResponse verifySoftToken(String softToken);
}
