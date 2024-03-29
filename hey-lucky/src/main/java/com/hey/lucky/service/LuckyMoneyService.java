package com.hey.lucky.service;

import com.hey.lucky.dto.user.CreateLuckyMoneyRequest;
import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.LuckyMoneyDetails;
import com.hey.lucky.dto.user.ReceiveLuckyMoneyRequest;
import com.hey.lucky.exception_handler.exception.*;

import java.util.List;

public interface LuckyMoneyService {
    void createLuckyMoney(CreateLuckyMoneyRequest createLuckyMoneyRequest) throws InternalServerErrException, ErrCallApiException, CannotTransferMoneyException, ErrCallChatApiException, UserNotInSessionChatException, MinAmountPerBagException;

    void receiveLuckyMoney(ReceiveLuckyMoneyRequest request) throws InvalidLuckyMoneyException, InternalServerErrException, ErrCallApiException, LuckyMoneyExpiredException, CannotTransferMoneyException, OutOfBagException, HadReceivedException, ErrCallChatApiException, UserNotInSessionChatException;

    List<LuckyMoneyDTO> getAllLuckyMoneyOfSession(String sessionId) throws InternalServerErrException, ErrCallApiException, CannotGetUserInfo, UserNotInSessionChatException;

    LuckyMoneyDetails getLuckyMoneyDetails(long luckyMoneyId) throws InvalidLuckyMoneyException, CannotGetUserInfo, InternalServerErrException, ErrCallApiException, UserNotInSessionChatException;

    void refundLuckyMoney();
}
