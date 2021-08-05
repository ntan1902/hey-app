package com.hey.auth.filter;

import com.hey.auth.entity.System;
import com.hey.auth.entity.User;
import com.hey.auth.exception.system.SystemIdNotFoundException;
import com.hey.auth.exception.user.UserIdNotFoundException;
import com.hey.auth.jwt.JwtSystemUtil;
import com.hey.auth.jwt.JwtUserUtil;
import com.hey.auth.service.SystemService;
import com.hey.auth.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserUtil jwtUserUtil;

    @Autowired
    private JwtSystemUtil jwtSystemUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemService systemService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Inside doFilterInternal of CustomAuthorizationFilter: {}", request.getServletPath());
        String path = request.getServletPath();
        if (!path.contains("/api/v1/systems/login") && !path.contains("/api/v1/users/login")) {
            if (path.contains("/api/v1/systems")) {
                try {
                    String jwt = getJwtFromRequest(request);
                    handleAuthorizationSystem(request, jwt);
                } catch (Exception e) {
                    log.error("Failed on set system authorization", e);
                }
            } else if (path.contains("/api/v1/users")) {
                try {
                    String jwt = getJwtFromRequest(request);
                    handleAuthorizationUser(request, jwt);
                } catch (Exception e) {
                    log.error("Failed on set user authorization", e);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private void handleAuthorizationUser(HttpServletRequest request, String jwt) throws UserIdNotFoundException {
        if (StringUtils.hasText(jwt) && jwtUserUtil.validateToken(jwt)) {
            String userId = jwtUserUtil.getUserIdFromJwt(jwt);

            User user = userService.loadUserById(userId);
            if (user != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                        user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private void handleAuthorizationSystem(HttpServletRequest request, String jwt) throws SystemIdNotFoundException {
        if (StringUtils.hasText(jwt) && jwtSystemUtil.validateToken(jwt)) {
            String systemId = jwtSystemUtil.getSystemIdFromJwt(jwt);

            System system = systemService.loadSystemById(systemId);
            if (system != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(system,
                        null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }
}
