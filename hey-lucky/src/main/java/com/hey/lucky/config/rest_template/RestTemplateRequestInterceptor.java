package com.hey.lucky.config.rest_template;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestTemplateRequestInterceptor implements ClientHttpRequestInterceptor {
    private final String token;

    public RestTemplateRequestInterceptor(String token) {
        this.token = token;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().setBearerAuth(token);
        return execution.execute(request, body);
    }
}
