package com.hey.auth.service;

public interface BlackListService {
    Boolean isExistInBlackListToken(String token);

    void deleteExpiredTokenInBlackList();

}
