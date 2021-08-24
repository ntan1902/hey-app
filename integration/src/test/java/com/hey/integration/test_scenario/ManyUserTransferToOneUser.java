package com.hey.integration.test_scenario;

import com.hey.integration.utils.RestTemplateUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static com.hey.integration.constants.Constant.*;

public class ManyUserTransferToOneUser extends Thread {

    private final RestTemplateUtil restTemplateUtil;
    private final String username;
    private final String password;
    private final String targetId;

    public ManyUserTransferToOneUser(RestTemplateUtil restTemplateUtil, String username, String password, String targetId) {
        this.restTemplateUtil = restTemplateUtil;
        this.username = username;
        this.password = password;
        this.targetId = targetId;
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
                postForEntity(LOGIN_URL, loginRequest, Map.class);


        @SuppressWarnings("unchecked")
        var payload = (Map<String, String>) Objects.requireNonNull(response.getBody()).get("payload");
        String token = payload.get("accessToken");

        // Set header for bearer token
        restTemplateUtil.setHeaders(restTemplate, token);

        String softToken = createSofToken(restTemplate);

        // create transfer
        var createTransferReq = new HashMap<String, Object>();
        createTransferReq.put("targetId", this.targetId);
        createTransferReq.put("message", "message ne");
        createTransferReq.put("softToken", softToken);
        restTemplate.postForEntity(CREATE_TRANSFER_URL, createTransferReq, Map.class);
    }

    private String createSofToken(RestTemplate restTemplate) {
        // create soft token
        Random random = new Random();
        var createSoftToken = new HashMap<String, Object>();
        createSoftToken.put("pin", "123456");
        createSoftToken.put("amount", random.nextInt(25_123) + 10_000);
        var createSoftTokenRes = restTemplate.
                postForEntity(CREATE_SOFT_TOKEN_URL, createSoftToken, Map.class);

        @SuppressWarnings("unchecked")
        var payloadCreateSoftToken = (Map<String, String>) Objects.requireNonNull(createSoftTokenRes.getBody()).get("payload");
        return payloadCreateSoftToken.get("softToken");
    }

}
