package com.hey.integration.utils;


import java.util.Map;

public interface RestTemplateUtil {
    void setHeaders(String token);

    Map<String, String> login(String username, String password);

    void register(String username, String fullName, String email, String password);

    void createWallet();

    void topUp(Long amount, String bankId);

    void createPin(String pin);

    void logout(String refreshToken);

    String createSofToken(String pin);

    void createTransfer(String targetId, String softToken);

    void addFriendRequest(String friendUsername);

    void acceptFriend(String friendUsername);

    void closeWaitingFriend(String friendId);
}
