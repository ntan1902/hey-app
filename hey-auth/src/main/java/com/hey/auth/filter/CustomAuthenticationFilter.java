package com.hey.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.auth.dto.api.ApiResponse;
import com.hey.auth.dto.user.LoginRequest;
import com.hey.auth.dto.user.LoginResponse;
import com.hey.auth.entity.RefreshToken;
import com.hey.auth.entity.User;
import com.hey.auth.jwt.JwtUserUtil;
import com.hey.auth.repository.RefreshTokenRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Log4j2
//@CrossOrigin("http://localhost:3000")
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    private JwtUserUtil jwtUserUtil;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            log.info("Inside login of CustomAuthenticationFilter: {}", loginRequest);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
            return this.getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.info("Authentication: {}", authResult);

        // Get User after Authenticate
        User user = (User) authResult.getPrincipal();

        // Set access token
        String accessToken = jwtUserUtil.generateAccessToken(user);

        // Set refresh token
        String refreshToken = jwtUserUtil.generateRefreshToken(user);
        refreshTokenRepository.save(new RefreshToken(refreshToken));

        LoginResponse payload = new LoginResponse(accessToken, refreshToken,"Bearer");
        // Response to client
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }
}
