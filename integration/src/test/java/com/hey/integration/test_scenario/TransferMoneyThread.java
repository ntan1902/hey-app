package com.hey.integration.test_scenario;

import com.hey.integration.utils.RestTemplateUtil;
import com.hey.integration.utils.RestTemplateUtilImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Random;

import static com.hey.integration.constants.Constant.*;

public class TransferMoneyThread extends Thread {

    private final String username;
    private final String password;
    private final String targetId;
    private final long amount;

    public TransferMoneyThread(String username, String password, String targetId, long amount) {
        this.username = username;
        this.password = password;
        this.targetId = targetId;
        this.amount = amount;
    }

    @Override
    public void run() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

        RestTemplateUtil restTemplateUtil = new RestTemplateUtilImpl(restTemplate);

        // Login
        restTemplateUtil.login(username, password);

        // Create soft token
        System.out.printf("User " + username + " transfer " + amount + " to " + targetId);
        String softToken = restTemplateUtil.createSofToken("123456", amount);

        // create transfer
        restTemplateUtil.createTransfer(this.targetId, softToken, amount);

        // Logout
        restTemplateUtil.logout();
    }


}
