package com.hey.integration.test_scenario;

import com.hey.integration.utils.RestTemplateUtil;
import com.hey.integration.utils.RestTemplateUtilImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

import static com.hey.integration.constants.Constant.BASE_URL;

public class AddFriendThread extends Thread {
    private final String username;
    private final String password;
    private final String friendUsername;

    public AddFriendThread(String username, String password, String friendUsername) {
        this.username = username;
        this.password = password;
        this.friendUsername = friendUsername;
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

        // Send add friend request
        restTemplateUtil.addFriendRequest(friendUsername);

        // Logout
        restTemplateUtil.logout(refreshToken);
    }


}
