package com.hey.lucky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "service")
public class ServiceProperties {
    private String chat;
    private String auth;
    private String payment;
    private String apiUrl;
    private String name;
    private String key;
}