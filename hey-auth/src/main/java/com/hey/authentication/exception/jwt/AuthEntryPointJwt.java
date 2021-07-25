package com.hey.authentication.exception.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.authentication.dto.api.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Log4j2
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        log.error("Unauthorized error: {}", e.getMessage());

        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);


        ObjectMapper objectMapper = new ObjectMapper();
        String resBody = objectMapper.writeValueAsString(ApiResponse.builder()
                .success(false)
                .code(HttpServletResponse.SC_UNAUTHORIZED)
                .message("Unauthorized")
                .payload("")
                .build()
        );
        PrintWriter printWriter = httpServletResponse.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }
}
