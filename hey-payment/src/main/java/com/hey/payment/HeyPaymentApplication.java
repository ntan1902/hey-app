package com.hey.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
@EnableCaching
public class HeyPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeyPaymentApplication.class, args);
    }

}
