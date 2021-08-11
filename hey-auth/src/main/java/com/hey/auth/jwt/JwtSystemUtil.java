package com.hey.auth.jwt;

import com.hey.auth.entity.System;
import com.hey.auth.properties.JwtProperties;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j2
@AllArgsConstructor
public class JwtSystemUtil {
    private final JwtProperties jwtProperties;

    public String generateToken(System system) {
        return doGenerateToken(system.getId());
    }

    public String doGenerateToken(String subject) {
        try {
            return Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(new Date(java.lang.System.currentTimeMillis()))
//                    .setExpiration(new Date(java.lang.System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                    .signWith(SignatureAlgorithm.HS256, jwtProperties.getSystemSecret())
                    .compact();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public String getSystemIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSystemSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSystemSecret()).parseClaimsJws(authToken);
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
