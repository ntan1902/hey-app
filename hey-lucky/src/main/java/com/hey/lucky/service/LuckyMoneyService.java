package com.hey.lucky.service;

import com.hey.lucky.dto.user.CreateLuckyMoneyRequest;
import com.hey.lucky.dto.user.ReceiveLuckyMoneyRequest;

public interface LuckyMoneyService {
    void createLuckyMoney(CreateLuckyMoneyRequest createLuckyMoneyRequest);

    void receiveLuckyMoney(ReceiveLuckyMoneyRequest request);
}
