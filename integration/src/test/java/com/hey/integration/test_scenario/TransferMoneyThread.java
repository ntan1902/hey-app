package com.hey.integration.test_scenario;

import com.hey.integration.utils.RestTemplateUtil;
import com.hey.integration.utils.RestTemplateUtilImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static com.hey.integration.constants.Constant.*;

public class TransferMoneyThread extends Thread {

    private final String username;
    private final String password;
    private final String targetId;

    public TransferMoneyThread(String username, String password, String targetId) {
        this.username = username;
        this.password = password;
        this.targetId = targetId;
    }

    @Override
    public void run() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

        RestTemplateUtil restTemplateUtil = new RestTemplateUtilImpl(restTemplate);

        // Login
        Map<String, String> payload = restTemplateUtil.login(username, password);

        String token = payload.get("accessToken");
        String refreshToken = payload.get("refreshToken");

        // Set header for bearer token
        restTemplateUtil.setHeaders(token);

        // Create soft token
        String softToken = restTemplateUtil.createSofToken("123456");

        // create transfer
        restTemplateUtil.createTransfer(this.targetId, softToken);

        // Logout
        restTemplateUtil.logout(refreshToken);
    }


}
