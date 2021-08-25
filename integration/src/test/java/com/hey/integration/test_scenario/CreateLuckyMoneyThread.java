package com.hey.integration.test_scenario;

import com.hey.integration.utils.RestTemplateUtil;
import com.hey.integration.utils.RestTemplateUtilImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

import static com.hey.integration.constants.Constant.BASE_URL;

public class CreateLuckyMoneyThread extends Thread{
    private String username;
    private String password;
    private String sessionId;

    public CreateLuckyMoneyThread(String username,String password, String sessionId){
        this.username = username;
        this.password = password;
        this.sessionId = sessionId;
    }

    @Override
    public void run() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

        RestTemplateUtil restTemplateUtil = new RestTemplateUtilImpl(restTemplate);

        restTemplateUtil.login(username,password);

        String type = "random";
        int numBag = 99;
        long amount = 50_000;

        // generate soft token;
        String softToken = restTemplateUtil.createSofToken("123456", amount);

        restTemplateUtil.createLuckyMoney(sessionId, type, numBag, softToken);

        restTemplateUtil.logout();


    }
}
