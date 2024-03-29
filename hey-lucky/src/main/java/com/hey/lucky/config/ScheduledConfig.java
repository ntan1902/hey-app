package com.hey.lucky.config;

import com.hey.lucky.service.LuckyMoneyService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@AllArgsConstructor
@Log4j2
public class ScheduledConfig {
    private final LuckyMoneyService luckyMoneyService;

    @Scheduled(fixedRate = 300_000, initialDelay = 60_000)
    public void refundLuckyMoney(){
        log.info("Refund money");
        luckyMoneyService.refundLuckyMoney();
    }
}
