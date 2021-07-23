package com.hey.authentication.jwt;

import com.hey.authentication.entity.System;
import com.hey.authentication.entity.User;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j2
public class JwtSystemUtil {
    @Value("${JWT_SYSTEM_SECRET}")
    private String JWT_SECRET;

    @Value("${JWT_EXPIRATION_MS}")
    private Long JWT_EXPIRATION;


    public String generateToken(System system) {
        return doGenerateToken(system.getId().toString());
    }

    public String doGenerateToken(String subject) {
        try {
            return Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(new Date(java.lang.System.currentTimeMillis()))
                    .setExpiration(new Date(java.lang.System.currentTimeMillis() + JWT_EXPIRATION))
                    .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                    .compact();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public Long getSystemIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        } catch (SignatureException ex) {
            log.error("JWT signature does not match locally computed signature");
        }
        return false;
    }
}