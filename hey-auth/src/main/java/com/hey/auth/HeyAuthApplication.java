package com.hey.auth;

import com.hey.auth.entity.System;
import com.hey.auth.repository.SystemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
@EnableEurekaClient
@EnableConfigurationProperties
public class HeyAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeyAuthApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(SystemRepository systemRepository) throws Exception {
        return args -> {
            systemRepository.saveAll(Arrays.asList(
                    System.builder()
                            .id("e8984aa8-b1a5-4c65-8c5e-036851ec780c")
                            .systemName("payment")
                            .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                            .numberOfWallet(0)
                            .build(),
                    System.builder()
                            .id("e8984aa8-b1a5-4c65-8c5e-036851ec781c")
                            .systemName("chat")
                            .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                            .numberOfWallet(0)
                            .build(),
                    System.builder()
                            .id("e8984aa8-b1a5-4c65-8c5e-036851ec782c")
                            .systemName("lucky_money")
                            .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                            .numberOfWallet(10)
                            .build(),
                    System.builder()
                            .id("e8984aa8-b1a5-4c65-8c5e-036851ec783c")
                            .systemName("bank")
                            .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                            .numberOfWallet(1)
                            .build()
            ));
        };
    }

}
