package com.hey.lucky.util;

import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.CannotGetUserInfo;
import com.hey.lucky.exception_handler.exception.ErrCallApiException;

public interface UserUtil {

    boolean isUserInSession(String userId, String sessionId) throws ErrCallApiException;

    UserInfo getUserInfo(String userId) throws CannotGetUserInfo;

    User getCurrentUser();
}
