package com.hey.lucky.service;

import com.hey.lucky.dto.user.*;
import com.hey.lucky.exception_handler.exception.InvalidLuckyMoneyException;

import java.util.List;

public interface LuckyMoneyService {
    void createLuckyMoney(CreateLuckyMoneyRequest createLuckyMoneyRequest);

    void receiveLuckyMoney(ReceiveLuckyMoneyRequest request) throws InvalidLuckyMoneyException;

    List<LuckyMoneyDTO> getAllLuckyMoney(String sessionId);

    LuckyMoneyDetails getDetailsLuckyMoney(long luckyMoneyId) throws InvalidLuckyMoneyException;

    void refundLuckyMoney();
}
