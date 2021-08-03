package com.hey.lucky.service;

import com.hey.lucky.dto.user.*;
import com.hey.lucky.exception_handler.exception.*;

import java.util.List;

public interface LuckyMoneyService {
    void createLuckyMoney(CreateLuckyMoneyRequest createLuckyMoneyRequest) throws UnauthorizeException, ErrCallApiException, CannotTransferMoneyException;

    void receiveLuckyMoney(ReceiveLuckyMoneyRequest request) throws InvalidLuckyMoneyException, UnauthorizeException, ErrCallApiException, LuckyMoneyExpiredException, CannotTransferMoneyException, OutOfBagException, HadReceivedException;

    List<LuckyMoneyDTO> getAllLuckyMoney(String sessionId) throws UnauthorizeException, ErrCallApiException;

    LuckyMoneyDetails getDetailsLuckyMoney(long luckyMoneyId) throws InvalidLuckyMoneyException, CannotGetUserInfo, UnauthorizeException, ErrCallApiException;

    void refundLuckyMoney();
}
