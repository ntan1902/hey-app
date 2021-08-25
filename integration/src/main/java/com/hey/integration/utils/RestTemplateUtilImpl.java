package com.hey.integration.utils;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.hey.integration.constants.Constant.*;

public class RestTemplateUtilImpl implements RestTemplateUtil {
    private final RestTemplate restTemplate;

    public RestTemplateUtilImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void setHeaders(String token) {
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add((request, body, execution) -> {
            request.getHeaders().setBearerAuth(token);
            return execution.execute(request, body);
        });
        restTemplate.setInterceptors(interceptors);
    }

    @Override
    public Map<String, String> login(String username, String password) {
        var loginRequest = new HashMap<String, String>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        var response = restTemplate.
                postForEntity( LOGIN_URL, loginRequest, Map.class);

        @SuppressWarnings("unchecked")
        var payload =  (Map<String, String>) Objects.requireNonNull(response.getBody()).get("payload");
        return payload;
    }

    @Override
    public void register(String username, String fullName, String email, String password) {
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", username);
        registerRequest.put("password", password);
        registerRequest.put("email", email);
        registerRequest.put("fullName", fullName);

        restTemplate.
                postForEntity( REGISTER_URL, registerRequest, Map.class);
    }

    @Override
    public void createWallet() {
        restTemplate.postForObject(CREATE_WALLET_URL, null, String.class);
    }

    @Override
    public void topUp(Long amount, String bankId) {
        var topUpRequest = new HashMap<String, Object>();
        topUpRequest.put("amount", AMOUNT);
        topUpRequest.put("bankId", BANK_ID);
        restTemplate.postForObject(TOP_UP_URL, topUpRequest, String.class);
    }

    @Override
    public void createPin(String pin) {
        var createPinReq = new HashMap<String, Object>();
        createPinReq.put("pin", pin);
        restTemplate.postForObject(CREATE_PIN_URL, createPinReq, String.class);
    }

    @Override
    public void logout(String refreshToken) {
        var logoutRequest = new HashMap<String, String>();
        logoutRequest.put("refreshToken", refreshToken);

        restTemplate.
                postForEntity( LOGOUT_URL, logoutRequest, Map.class);
    }

    @Override
    public String createSofToken(String pin) {
        // create soft token
        Random random = new Random();
        var createSoftToken = new HashMap<String, Object>();
        createSoftToken.put("pin", pin);
        createSoftToken.put("amount", random.nextInt(25_123) + 10_000);
        var createSoftTokenRes = restTemplate.
                postForEntity(CREATE_SOFT_TOKEN_URL, createSoftToken, Map.class);

        @SuppressWarnings("unchecked")
        var payloadCreateSoftToken = (Map<String, String>) Objects.requireNonNull(createSoftTokenRes.getBody()).get("payload");
        return payloadCreateSoftToken.get("softToken");
    }

    @Override
    public void createTransfer(String targetId, String softToken) {
        var createTransferReq = new HashMap<String, Object>();
        createTransferReq.put("targetId", targetId);
        createTransferReq.put("message", "message ne");
        createTransferReq.put("softToken", softToken);
        restTemplate.postForEntity(CREATE_TRANSFER_URL, createTransferReq, Map.class);
    }
}
