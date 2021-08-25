package com.hey.integration;

import com.hey.integration.utils.RestTemplateUtil;
import com.hey.integration.utils.RestTemplateUtilImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

import static com.hey.integration.constants.Constant.*;

@SpringBootTest
public class RegisterTests {
    private RestTemplateUtil restTemplateUtil;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

        restTemplateUtil = new RestTemplateUtilImpl(restTemplate);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/Data_100.csv", numLinesToSkip = 1)
    void register(String username, String fullName, String email, String password) {

        // Register
        restTemplateUtil.register(username, fullName, email, password);

        // Login
        Map<String, String> payload = restTemplateUtil.login(username, password);

        String token = payload.get("accessToken");
        String refreshToken = payload.get("refreshToken");


        // Set header for bearer token
        restTemplateUtil.setHeaders(token);

        // Create Wallet
        restTemplateUtil.createWallet();

        // Top up
        restTemplateUtil.topUp(AMOUNT, BANK_ID);

        // Create Pin
        restTemplateUtil.createPin("123456");

        // Logout
        restTemplateUtil.logout(refreshToken);
    }



}
