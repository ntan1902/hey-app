package com.hey.lucky.service;

import com.hey.lucky.dto.user.*;

import java.util.List;

public interface LuckyMoneyService {
    void createLuckyMoney(CreateLuckyMoneyRequest createLuckyMoneyRequest);

    void receiveLuckyMoney(ReceiveLuckyMoneyRequest request);

    List<LuckyMoneyDTO> getAllLuckyMoney(String sessionId);

    LuckyMoneyDetails getDetailsLuckyMoney(long luckyMoneyId);

    void refundLuckyMoney();
}
