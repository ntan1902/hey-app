package com.hey.integration.test_scenario;

import com.hey.integration.utils.RestTemplateUtil;
import com.hey.integration.utils.RestTemplateUtilImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

import static com.hey.integration.constants.Constant.BASE_URL;

public class ReceiveLuckyMoneyThread extends Thread {
    private String username;
    private String password;
    private String sessionId;

    public ReceiveLuckyMoneyThread(String username, String password, String sessionId) {
        this.username = username;
        this.password = password;
        this.sessionId = sessionId;
    }

    @Override
    public void run() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

        RestTemplateUtil restTemplateUtil = new RestTemplateUtilImpl(restTemplate);

        restTemplateUtil.login(username, password);

        Map<String, Object> getLuckyMoneyRes = restTemplateUtil.getLuckyMoneyOfSession(sessionId);
        List<Map<String, Object>> payload = (List<Map<String, Object>>) getLuckyMoneyRes.get("payload");

        payload.forEach(luckyMoney -> {
            restTemplateUtil.receiveLuckyMoney(Long.parseLong(String.valueOf(luckyMoney.get("luckyMoneyId"))));
        });

        restTemplateUtil.logout();


    }
}
