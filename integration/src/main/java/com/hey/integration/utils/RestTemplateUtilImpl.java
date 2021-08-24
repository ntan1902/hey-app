package com.hey.integration.utils;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class RestTemplateUtilImpl implements RestTemplateUtil {
    @Override
    public void setHeaders(RestTemplate restTemplate, String token) {
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add((request, body, execution) -> {
            request.getHeaders().setBearerAuth(token);
            return execution.execute(request, body);
        });
        restTemplate.setInterceptors(interceptors);
    }
}
