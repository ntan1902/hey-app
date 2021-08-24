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

@SpringBootTest
public class RegisterTests {
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    private final static String BASE_URL = "http://localhost:5050";
    private final static String REGISTER_URL = "/auth/api/v1/users/register";
    private final static String LOGIN_URL = "/auth/api/v1/users/login";
    private final static String CREATE_WALLET_URL = "/payment/api/v1/me/createWallet";
    private final static String TOP_UP_URL = "/payment/api/v1/me/topup";
    private final static String PAYLOAD = "payload";
    private final static String ACCESS_TOKEN = "accessToken";
    private final static String BANK_ID = "e8984aa8-b1a5-4c65-8c5e-036851ec783c";
    private final static Long AMOUNT = 2_000_000L;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/RegisterData.csv", numLinesToSkip = 1)
    void register(String username, String password, String email, String fullName) {

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
        var payload =  (Map<String, String>) Objects.requireNonNull(response.getBody()).get(PAYLOAD);
        String token = payload.get(ACCESS_TOKEN);

        // Set header for bearer token
        restTemplateUtil.setHeaders(restTemplate, token);

        // Create Wallet
        restTemplate.postForObject(CREATE_WALLET_URL, null, String.class);

        // Top up
        var topUpRequest = new HashMap<String, Object>();
        topUpRequest.put("amount", AMOUNT);
        topUpRequest.put("bankId", BANK_ID);
        restTemplate.postForObject(TOP_UP_URL, topUpRequest, String.class);
    }
}
