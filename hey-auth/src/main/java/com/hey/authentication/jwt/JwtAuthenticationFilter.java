package com.hey.authentication.jwt;


import com.hey.authentication.entity.System;
import com.hey.authentication.entity.User;
import com.hey.authentication.service.SystemService;
import com.hey.authentication.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserUtil jwtUserUtil;

    @Autowired
    private JwtSystemUtil jwtSystemUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemService systemService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("Inside doFilterInternal of JwtUserFilter: {}", request.getServletPath());

        if(request.getServletPath().contains("/api/v1/systems")) {
            try {
                String jwt = getJwtFromRequest(request);

                if (StringUtils.hasText(jwt) && jwtSystemUtil.validateToken(jwt)) {
                    Long systemId = jwtSystemUtil.getSystemIdFromJwt(jwt);

                    System system = systemService.loadSystemById(systemId);
                    if (system != null) {
                        UsernamePasswordAuthenticationToken
                                authentication = new UsernamePasswordAuthenticationToken(system, null, null);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                log.error("Failed on set system authentication", e);
            }
        } else if (request.getServletPath().contains("/api/v1/users")){
            try {
                String jwt = getJwtFromRequest(request);

                if (StringUtils.hasText(jwt) && jwtUserUtil.validateToken(jwt)) {
                    Long userId = jwtUserUtil.getUserIdFromJwt(jwt);

                    User user = userService.loadUserById(userId);
                    if (user != null) {
                        UsernamePasswordAuthenticationToken
                                authentication = new UsernamePasswordAuthenticationToken(user,
                                null,
                                user.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                log.error("Failed on set user authentication", e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
