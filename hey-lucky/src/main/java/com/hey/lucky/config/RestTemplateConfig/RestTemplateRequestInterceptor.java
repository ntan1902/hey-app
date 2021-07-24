package com.hey.lucky.config.RestTemplateConfig;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestTemplateRequestInterceptor implements ClientHttpRequestInterceptor {
    private String jwtService;
    public RestTemplateRequestInterceptor(String jwtService){
        this.jwtService = jwtService;
    }
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add("Authorization",jwtService);
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }
}
