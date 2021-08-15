package com.hey.lucky.service;

import com.hey.lucky.dto.user.*;
import com.hey.lucky.exception_handler.exception.*;

import java.util.List;

public interface LuckyMoneyService {
    void createLuckyMoney(CreateLuckyMoneyRequest createLuckyMoneyRequest) throws UnauthorizeException, ErrCallApiException, CannotTransferMoneyException, ErrCallChatApiException, UserNotInSessionChatException;

    void receiveLuckyMoney(ReceiveLuckyMoneyRequest request) throws InvalidLuckyMoneyException, UnauthorizeException, ErrCallApiException, LuckyMoneyExpiredException, CannotTransferMoneyException, OutOfBagException, HadReceivedException, ErrCallChatApiException, UserNotInSessionChatException;

    List<LuckyMoneyDTO> getAllLuckyMoneyOfSession(String sessionId) throws UnauthorizeException, ErrCallApiException, CannotGetUserInfo, UserNotInSessionChatException;

    LuckyMoneyDetails getLuckyMoneyDetails(long luckyMoneyId) throws InvalidLuckyMoneyException, CannotGetUserInfo, UnauthorizeException, ErrCallApiException, UserNotInSessionChatException;

    void refundLuckyMoney();
}
