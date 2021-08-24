package com.hey.integration.test_scenario;

import com.hey.integration.utils.RestTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.hey.integration.constants.Constant.*;

public class OneUserTransferToNUser implements Runnable{

    private RestTemplateUtil restTemplateUtil;
    private String username;
    private String password;

    @Override
    public void run() {
        RestTemplate restTemplate = new RestTemplate();
        // Login
        var loginRequest = new HashMap<String, String>();
        loginRequest.put("username", this.username);
        loginRequest.put("password", this.password);

        var response = restTemplate.
                postForEntity( LOGIN_URL, loginRequest, Map.class);

        var payload =  (Map<String, String>) Objects.requireNonNull(response.getBody()).get(PAYLOAD);
        String token = payload.get(ACCESS_TOKEN);

        // Set header for bearer token
        restTemplateUtil.setHeaders(restTemplate, token);

//        restTemplate.postForEntity()
    }


}
