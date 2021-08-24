package com.hey.integration.utils;

import org.springframework.web.client.RestTemplate;

public interface RestTemplateUtil {
    void setHeaders(RestTemplate restTemplate, String token);
}
