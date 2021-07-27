package com.hey.lucky.config.RestTemplateConfig;


import com.hey.lucky.dto.auth_service.LoginRequest;
import com.hey.lucky.dto.auth_service.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class RestTemplateConfig {
    @Value("${AUTH_SERVICE}")
    private String AUTH_SERVICE;

    @Value("${AUTH_API_VER}")
    private String AUTH_API_VER;


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        configMapperWebClient(restTemplate);

        addErrHandler(restTemplate);

        loginToAuthService(restTemplate);

        return restTemplate;
    }

    public void configMapperWebClient(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter map = new MappingJackson2HttpMessageConverter();
        messageConverters.add(map);
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
    }

    public void loginToAuthService(RestTemplate restTemplate) {
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(AUTH_SERVICE + AUTH_API_VER));
        HttpEntity<LoginRequest> authRequestHttpEntity = new HttpEntity<>(new LoginRequest("lucky_money", "123456"));
        LoginResponse loginResponse = restTemplate.postForObject("/login", authRequestHttpEntity, LoginResponse.class);
        String jwtService = loginResponse.getPayload().getTokenType() + " " + loginResponse.getPayload().getAccessToken();
        restTemplate.setInterceptors(Collections.singletonList(new RestTemplateRequestInterceptor(jwtService)));
    }

    public void addErrHandler(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(new RestTemplateErrHandler());
    }
}
