package com.hey.payment.config.rest_template;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestTemplateRequestInterceptor implements ClientHttpRequestInterceptor {
    private final String jwtService;

    public RestTemplateRequestInterceptor(String jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add("Authorization", jwtService);
        return execution.execute(request, body);
    }
}
