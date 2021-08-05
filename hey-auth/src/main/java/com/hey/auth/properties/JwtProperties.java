package com.hey.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String userSecret;
    private String systemSecret;
    private Long expirationMs;

    private String softTokenSecret;
    private Long softTokenExpirationMs;
}
