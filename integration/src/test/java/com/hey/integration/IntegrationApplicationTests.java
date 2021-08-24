package com.hey.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class IntegrationApplicationTests {
    public final static String LOGIN_URL = "http://localhost:7070/auth/api/v1/users/login";
    public final static String GET_INFO_URL = "http://localhost:7070/auth/api/v1/users/getInfo";
    public final static String PAYLOAD = "payload";
    public final static String ACCESS_TOKEN = "accessToken";

    @Test
    void contextLoads() {
        RestTemplate testRestTemplate = new RestTemplate();

        // Login
        Map<String, String> request = new HashMap<>();
        request.put("username", "an");
        request.put("password", "123456");

        var response = testRestTemplate.
                postForEntity( LOGIN_URL, request, Map.class);

        @SuppressWarnings("unchecked")
        var payload =  (Map<String, String>) Objects.requireNonNull(response.getBody()).get(PAYLOAD);
        String token = payload.get(ACCESS_TOKEN);

        // Set header for bearer token
        setHeaders(testRestTemplate, token);

        // Get info
        ResponseEntity<String> getProfile = testRestTemplate
                .getForEntity(GET_INFO_URL, String.class);

        System.out.println(getProfile);
    }

    private void setHeaders(RestTemplate testRestTemplate, String token) {
        List<ClientHttpRequestInterceptor> interceptors = testRestTemplate.getInterceptors();
        interceptors.add((request, body, execution) -> {
            request.getHeaders().setBearerAuth(token);
            return execution.execute(request, body);
        });
        testRestTemplate.setInterceptors(interceptors);
    }

}
