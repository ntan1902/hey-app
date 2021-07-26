package com.hey.authentication.config;


import com.hey.authentication.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Bean
    public JwtAuthenticationFilter jwtUserAuthenticationFiler() {
        return new JwtAuthenticationFilter();
    }
//
//    @Bean
//    public JwtSystemAuthenticationFilter jwtSystemAuthenticationFilter() {
//        return new JwtSystemAuthenticationFilter();
//    }
}
