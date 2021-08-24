package com.hey.integration;

import com.hey.integration.utils.RestTemplateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.hey.integration.constants.Constant.*;

@SpringBootTest
public class RegisterTests {
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplateUtil restTemplateUtil;


    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/RegisterData.csv", numLinesToSkip = 1)
    void register(String username, String fullName, String email, String password) {

        // Register
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", username);
        registerRequest.put("password", password);
        registerRequest.put("email", email);
        registerRequest.put("fullName", fullName);

        restTemplate.
                postForEntity( REGISTER_URL, registerRequest, Map.class);

        // Login
        var loginRequest = new HashMap<String, String>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        var response = restTemplate.
                postForEntity( LOGIN_URL, loginRequest, Map.class);

        @SuppressWarnings("unchecked")
        var payload =  (Map<String, String>) Objects.requireNonNull(response.getBody()).get("payload");
        String token = payload.get("accessToken");

        // Set header for bearer token
        restTemplateUtil.setHeaders(restTemplate, token);

        // Create Wallet
        restTemplate.postForObject(CREATE_WALLET_URL, null, String.class);

        // Top up
        var topUpRequest = new HashMap<String, Object>();
        topUpRequest.put("amount", AMOUNT);
        topUpRequest.put("bankId", BANK_ID);
        restTemplate.postForObject(TOP_UP_URL, topUpRequest, String.class);

        // Create Pin
        var createPinReq = new HashMap<String, Object>();
        createPinReq.put("pin", "123456");
        restTemplate.postForObject(CREATE_PIN_URL, createPinReq, String.class);
    }
}
