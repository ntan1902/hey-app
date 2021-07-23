package com.hey.payment.api;

import com.hey.payment.dto.auth_service.SoftTokenEncoded;
import com.hey.payment.dto.auth_service.SystemInfo;
import com.hey.payment.dto.auth_service.UserInfo;
import com.hey.payment.dto.user.ApiResponse;

public interface AuthApi {
    ApiResponse<UserInfo> getUserInfo(long userId);
    ApiResponse<SystemInfo> getSystemInfo(long systemId);
    ApiResponse<SoftTokenEncoded> verifySoftToken(String softToken);
}
