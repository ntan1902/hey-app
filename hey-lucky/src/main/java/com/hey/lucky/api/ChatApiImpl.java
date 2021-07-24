package com.hey.lucky.api;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class ChatApiImpl implements ChatApi{

    private final RestTemplate restTemplate;

}
