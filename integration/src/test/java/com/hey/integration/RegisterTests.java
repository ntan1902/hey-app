package com.hey.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class RegisterTests {
    private RestTemplate restTemplate;
    private final static String REGISTER_URL = "http://localhost:7070/auth/api/v1/users/register";

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/RegisterData.csv", numLinesToSkip = 1)
    void register(String username, String password, String email, String fullName) {

        // Login
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);
        request.put("email", email);
        request.put("fullName", fullName);

        var response = restTemplate.
                postForEntity( REGISTER_URL, request, Map.class);

//        @SuppressWarnings("unchecked")
//        var payload =  (Map<String, String>) Objects.requireNonNull(response.getBody()).get(PAYLOAD);
//        String token = payload.get(ACCESS_TOKEN);
//
//        // Set header for bearer token
//        setHeaders(restTemplate, token);
//
//        // Get info
//        ResponseEntity<String> getProfile = restTemplate
//                .getForEntity(GET_INFO_URL, String.class);

        System.out.println(response);
    }
}
