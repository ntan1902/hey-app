package com.hey.lucky.api;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class AuthApiImpl implements AuthApi {
    private final RestTemplate restTemplate;

}
