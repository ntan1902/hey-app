package com.hey.lucky.authentication;

import com.hey.lucky.dto.auth_service.AuthorizeUserRequest;
import com.hey.lucky.dto.auth_service.AuthorizeUserResponse;
import com.hey.lucky.entity.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Log4j2
@Component
public class AuthServiceFilter extends OncePerRequestFilter {
    private final HttpStatus[] ERR_CODE = {HttpStatus.UNAUTHORIZED, HttpStatus.BAD_REQUEST};

    private final RestTemplate restTemplate;

    @Autowired
    public AuthServiceFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);
            if (StringUtils.hasText(token)) {
                UsernamePasswordAuthenticationToken authenticationToken = null;
                log.info("Authorize user with token {}", token);
                User user = new User(authorizeUser(token));
                authenticationToken = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            log.error("Failed on set user authentication", e);
        }
        filterChain.doFilter(request, response);
    }


    private long authorizeUser(String token) {
        HttpEntity<AuthorizeUserRequest> requestEntity = new HttpEntity<>(new AuthorizeUserRequest(token));
        AuthorizeUserResponse authorizeUserResponse = restTemplate.postForObject("/authorizeUser", requestEntity, AuthorizeUserResponse.class);
        return authorizeUserResponse.getPayload().getUserId();
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
