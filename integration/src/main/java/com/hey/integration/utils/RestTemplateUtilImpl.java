package com.hey.integration.utils;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class RestTemplateUtil {
    private void setHeaders(RestTemplate testRestTemplate, String token) {
        List<ClientHttpRequestInterceptor> interceptors = testRestTemplate.getInterceptors();
        interceptors.add((request, body, execution) -> {
            request.getHeaders().setBearerAuth(token);
            return execution.execute(request, body);
        });
        testRestTemplate.setInterceptors(interceptors);
    }
}
