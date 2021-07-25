package com.hey.lucky.config.RestTemplateConfig;


import com.hey.lucky.dto.auth_service.LoginRequest;
import com.hey.lucky.dto.auth_service.LoginResponse;
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
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        configMapperWebClient(restTemplate);

        addErrHandler(restTemplate);

        loginToAuthService(restTemplate);

        return restTemplate;
    }

    public void configMapperWebClient(RestTemplate restTemplate){
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        MappingJackson2HttpMessageConverter map = new MappingJackson2HttpMessageConverter();
        messageConverters.add(map);
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
    }

    public void loginToAuthService(RestTemplate restTemplate){
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://89268aa87b25.ap.ngrok.io/api/v1/systems"));
        HttpEntity<LoginRequest> authRequestHttpEntity = new HttpEntity<>(new LoginRequest("payment", "123456"));
        LoginResponse loginResponse = restTemplate.postForObject("/login", authRequestHttpEntity, LoginResponse.class);
        String jwtService = loginResponse.getPayload().getTokenType() + " " + loginResponse.getPayload().getAccessToken();
        restTemplate.setInterceptors(Collections.singletonList(new RestTemplateRequestInterceptor(jwtService)));
    }

    public void addErrHandler(RestTemplate restTemplate){
        restTemplate.setErrorHandler(new RestTemplateErrHandler());
    }
}
