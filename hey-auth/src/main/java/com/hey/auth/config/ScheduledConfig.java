package com.hey.auth.config;

import com.hey.auth.service.BlackListService;
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
    private final BlackListService blackListService;

    @Scheduled(fixedRate = 300_000)
    public void deleteExpiredTokenInBlackList(){
        log.info("Inside deleteExpiredTokenInBlackList of ScheduledConfig");
        blackListService.deleteExpiredTokenInBlackList();

    }
}