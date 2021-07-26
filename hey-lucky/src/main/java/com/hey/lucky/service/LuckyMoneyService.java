package com.hey.lucky.service;

import com.hey.lucky.dto.user.*;

import java.util.List;

public interface LuckyMoneyService {
    void createLuckyMoney(CreateLuckyMoneyRequest createLuckyMoneyRequest);

    void receiveLuckyMoney(ReceiveLuckyMoneyRequest request);

    List<LuckyMoneyDTO> getAllLuckyMoney(GetAllLuckyMoneyRequest request);

    LuckyMoneyDetails getDetailsLuckyMoney(GetDetailsLuckyMoneyRequest request);
}
