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
        restTemplateUtil.login(username, password);

        // Create soft token
        Random random = new Random();
        String softToken = restTemplateUtil.createSofToken("123456", random.nextInt(25_123) + 10_000);

        // create transfer
        restTemplateUtil.createTransfer(this.targetId, softToken);

        // Logout
        restTemplateUtil.logout();
    }


}
