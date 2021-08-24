package com.hey.integration.test_scenario;

import com.hey.integration.utils.RestTemplateUtil;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static com.hey.integration.constants.Constant.*;

public class NUserTransferToOneUser extends Thread {

    private RestTemplateUtil restTemplateUtil;
    private String username;
    private String password;
    private String targetId;

    private volatile long balance;

    public NUserTransferToOneUser(RestTemplateUtil restTemplateUtil, String username, String password, String targetId) {
        this.restTemplateUtil = restTemplateUtil;
        this.username = username;
        this.password = password;
        this.targetId = targetId;
    }

    public long getBalance() {
        return balance;
    }

    @Override
    public void run() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

        // Login
        var loginRequest = new HashMap<String, String>();
        loginRequest.put("username", this.username);
        loginRequest.put("password", this.password);

        var response = restTemplate.
                postForEntity( LOGIN_URL, loginRequest, Map.class);


        @SuppressWarnings("unchecked")
        var payload = (Map<String, String>) Objects.requireNonNull(response.getBody()).get(PAYLOAD);
        String token = payload.get(ACCESS_TOKEN);

        // Set header for bearer token
        restTemplateUtil.setHeaders(restTemplate, token);

        String softToken = createSofToken(restTemplate);

        // create transfer
        var createTransferReq = new HashMap<String, Object>();
        createTransferReq.put("targetId", this.targetId);
        createTransferReq.put("message", "message ne");
        createTransferReq.put("softToken", softToken);
        restTemplate.postForEntity(CREATE_TRANSFER_URL, createTransferReq, Map.class);

        // get balance
        var getBalanceRes = restTemplate.getForObject(GET_WALLET_URL, Map.class);
        Map<String, String> pl = (Map<String, String>) getBalanceRes.get("payload");
        balance = Long.parseLong(pl.get("balance"));
    }

    private String createSofToken(RestTemplate restTemplate) {
        // create soft token
        Random random = new Random();
        var createSoftToken = new HashMap<String, Object>();
        createSoftToken.put("pin", "123456");
        createSoftToken.put("amount", random.nextInt(25_000)+10_000);
        var createSoftTokenRes = restTemplate.
                postForEntity(CREATE_SOFT_TOKEN_URL, createSoftToken, Map.class);
        @SuppressWarnings("unchecked")
        var payloadCreateSoftToken = (Map<String, String>) Objects.requireNonNull(createSoftTokenRes.getBody()).get(PAYLOAD);
        String softToken = payloadCreateSoftToken.get("softToken");
        return softToken;
    }

}
