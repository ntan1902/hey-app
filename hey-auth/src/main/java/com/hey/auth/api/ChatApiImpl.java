package com.hey.auth.api;

import com.hey.auth.dto.vertx.RegisterRequestToChat;
import com.hey.auth.entity.User;
import com.hey.auth.mapper.UserMapper;
import com.hey.auth.properties.ServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Log4j2
@AllArgsConstructor
public class ChatApiImpl implements ChatApi {
    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;
    private final UserMapper userMapper;

    @Override
    public void register(User user) {
        log.info("Inside register of ChatApiImpl: {}", user);
        RegisterRequestToChat registerRequestToChat = userMapper.registerRequest2Chat(user);
        restTemplate.postForObject(serviceProperties.getChat() + "/api/public/register", registerRequestToChat, Void.class);
    }
}
