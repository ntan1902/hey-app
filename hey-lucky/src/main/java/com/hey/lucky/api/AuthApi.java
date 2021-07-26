package com.hey.lucky.api;

import com.hey.lucky.dto.auth_service.GetUserInfoResponse;

public interface AuthApi {
    GetUserInfoResponse getUserInfo(long userId);
}
