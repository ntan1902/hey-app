package com.hey.auth;

import com.hey.auth.entity.User;
import com.hey.auth.repository.UserRepository;
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
    public CommandLineRunner run(UserRepository userRepository) throws Exception {
        return args -> {
            userRepository.saveAll(Arrays.asList(
                    User.builder()
                            .id("e8984aa8-b1a5-4c65-8c5e-036851ec772c")
                            .email("ntan1902@gmail.com")
                            .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                            .fullName("Nguyen Trinh An")
                            .username("ntan")
                            .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                            .build(),
                    User.builder()
                            .id("e8984aa8-b1a5-4c65-8c5e-036851ec773c")
                            .email("trong@gmail.com")
                            .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                            .fullName("Vo Ngoc Trong")
                            .username("trong")
                            .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                            .build()
                    )
            );
        };
    }

}
