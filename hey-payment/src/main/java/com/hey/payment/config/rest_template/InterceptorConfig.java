package com.hey.payment.config.rest_template;

import com.hey.payment.dto.auth_system.LoginRequest;
import com.hey.payment.dto.auth_system.LoginResponse;
import com.hey.payment.properties.ServiceProperties;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class InterceptorConfig {
    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    @EventListener(ApplicationStartedEvent.class)
    public void setInterceptorForRestTemplate() {
        configMapperWebClient();

        addErrHandler();

        loginToAuthService();

    }

    public void configMapperWebClient() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter map = new MappingJackson2HttpMessageConverter();
        messageConverters.add(map);
        messageConverters.add(new FormHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
    }

    public void loginToAuthService() {
        log.info("Inside loginToAuthService");
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(serviceProperties.getAuth() + serviceProperties.getApiUrl()));
        HttpEntity<LoginRequest> authRequestHttpEntity = new HttpEntity<>(new LoginRequest(serviceProperties.getName(), serviceProperties.getKey()));
        LoginResponse loginResponse = restTemplate.postForObject("/login", authRequestHttpEntity, LoginResponse.class);
        log.info("Login response: {}", loginResponse);

        String token = null;
        if (loginResponse != null) {
            token = loginResponse.getPayload().getAccessToken();
        }

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new RestTemplateRequestInterceptor(token));
        restTemplate.setInterceptors(interceptors);
    }

    public void addErrHandler() {
        restTemplate.setErrorHandler(new RestTemplateErrHandler());
    }


}
