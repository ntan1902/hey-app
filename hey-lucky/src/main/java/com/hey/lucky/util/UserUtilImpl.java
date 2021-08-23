package com.hey.lucky.util;

import com.hey.lucky.api.AuthApi;
import com.hey.lucky.api.ChatApi;
import com.hey.lucky.dto.auth_service.GetUserInfoResponse;
import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.chat_service.CheckUserInSessionChatRequest;
import com.hey.lucky.dto.chat_service.CheckUserInSessionChatResponse;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.CannotGetUserInfo;
import com.hey.lucky.exception_handler.exception.ErrCallApiException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class UserUtilImpl implements UserUtil{

    private final ChatApi chatApi;
    private final AuthApi authApi;

    public UserUtilImpl(ChatApi chatApi, AuthApi authApi) {
        this.chatApi = chatApi;
        this.authApi = authApi;
    }

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    @Override
    public boolean isUserInSession(String userId, String sessionId) throws ErrCallApiException {
        log.info("Check user {} is in group chat {}", userId, sessionId);
        CheckUserInSessionChatResponse response = chatApi.checkUserInSessionChat(new CheckUserInSessionChatRequest(userId, sessionId));
        if (!response.isSuccess()) {
            throw new ErrCallApiException("Can not verify your authentication. Try later!");
        }
        return response.getPayload().isExisted();
    }
    @Override
    public UserInfo getUserInfo(String userId) throws CannotGetUserInfo {
        log.info("Get user info from auth service");
        GetUserInfoResponse apiResponse = authApi.getUserInfo(userId);
        if (!apiResponse.getSuccess()) {
            throw new CannotGetUserInfo();
        }
        return apiResponse.getPayload();
    }
}
