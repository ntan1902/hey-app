package com.hey.integration.utils;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.hey.integration.constants.Constant.*;

public class RestTemplateUtilImpl implements RestTemplateUtil {
    private final RestTemplate restTemplate;
    private String refreshToken;

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

        String token = payload.get("accessToken");

        refreshToken = payload.get("refreshToken");
        setHeaders(token);

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
    public void logout() {
        var logoutRequest = new HashMap<String, String>();
        logoutRequest.put("refreshToken", refreshToken);

        restTemplate.
                postForEntity( LOGOUT_URL, logoutRequest, Map.class);
    }

    @Override
    public String createSofToken(String pin, long amount) {
        // create soft token
        var createSoftToken = new HashMap<String, Object>();
        createSoftToken.put("pin", pin);
        createSoftToken.put("amount", amount);
        var createSoftTokenRes = restTemplate.
                postForEntity(CREATE_SOFT_TOKEN_URL, createSoftToken, Map.class);

        @SuppressWarnings("unchecked")
        var payloadCreateSoftToken = (Map<String, String>) Objects.requireNonNull(createSoftTokenRes.getBody()).get("payload");
        return payloadCreateSoftToken.get("softToken");
    }

    @Override
    public void createTransfer(String targetId, String softToken, long amount) {
        var createTransferReq = new HashMap<String, Object>();
        createTransferReq.put("targetId", targetId);
        createTransferReq.put("message", "message ne");
        createTransferReq.put("softToken", softToken);
        createTransferReq.put("amount", amount);
        restTemplate.postForEntity(CREATE_TRANSFER_URL, createTransferReq, Map.class);
    }

    @Override
    public void addFriendRequest(String friendUsername) {
        var addFriendReq = new HashMap<String, Object>();
        addFriendReq.put("username", friendUsername);
        restTemplate.postForEntity(ADD_FRIEND_REQUEST_URL, addFriendReq, Map.class);
    }

    @Override
    public void acceptFriend(String friendUsername) {
        var addFriendReq = new HashMap<String, Object>();
        addFriendReq.put("username", friendUsername);
        restTemplate.postForEntity(ACCEPT_FRIEND_REQUEST_URL, addFriendReq, Map.class);
    }

    @Override
    public void closeWaitingFriend(String friendId) {
        var closeFriendReq = new HashMap<String, Object>();
        closeFriendReq.put("userId", friendId);
        restTemplate.postForEntity(CLOSE_FRIEND_REQUEST_URL, closeFriendReq, Map.class);
    }

    @Override
    public Map<String, Object> getChatList() {
        var getChatListReq = new HashMap<String, Object>();
        return restTemplate.postForObject(GET_CHAT_LIST_REQUEST_URL, getChatListReq, Map.class);
    }

    @Override
    public Map<String, Object> getLuckyMoneyOfSession(String sessionId) {
        return restTemplate.getForObject(GET_LUCKY_MONEY_OF_SESSION_URL+"?sessionId="+sessionId,Map.class);
    }

    @Override
    public void createLuckyMoney(String sessionId, String type, int numBag, String softToken, long amount) {
        var createLuckyMoneyReq = new HashMap<String, Object>();
        createLuckyMoneyReq.put("sessionChatId",sessionId);
        createLuckyMoneyReq.put("type",type);
        createLuckyMoneyReq.put("numberBag",numBag);
        createLuckyMoneyReq.put("message", "chuc mung");
        createLuckyMoneyReq.put("amount", amount);
        createLuckyMoneyReq.put("softToken",softToken);
        restTemplate.postForEntity(CREATE_LUCKY_MONEY_URL,createLuckyMoneyReq,Map.class);
    }

    @Override
    public void receiveLuckyMoney(Long luckyMoneyId) {
        var receiveLuckyMoney = new HashMap<String, Long>();
        receiveLuckyMoney.put("luckyMoneyId",luckyMoneyId);
        restTemplate.postForEntity(RECEIVE_LUCKY_MONEY_URL,receiveLuckyMoney,Map.class);
    }
}
