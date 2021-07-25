package com.hey.authentication;

import com.hey.authentication.entity.User;
import com.hey.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class AuthenticationApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        userRepository.saveAll(Arrays.asList(
                User.builder()
                        .email("ntan1902@gmail.com")
                        .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                        .fullName("Nguyen Trinh An")
                        .username("ntan")
                        .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                        .build(),
                User.builder()
                        .email("trong@gmail.com")
                        .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                        .fullName("Vo Ngoc Trong")
                        .username("trong")
                        .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                        .build()
                )
        );
    }
}
