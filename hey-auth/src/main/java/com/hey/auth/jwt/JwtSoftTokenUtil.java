package com.hey.auth.jwt;

import com.hey.auth.entity.User;
import com.hey.auth.properties.JwtProperties;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class JwtSoftTokenUtil {
    private final JwtProperties jwtProperties;


    public String generateToken(User user, String pin, long amount) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", UUID.randomUUID().toString());
        claims.put("pin", pin);
        claims.put("amount", amount);

        return doGenerateToken(claims, user.getId());
    }

    public String doGenerateToken(Map<String, Object> claims, String subject) {
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(java.lang.System.currentTimeMillis()))
                    .setExpiration(new Date(java.lang.System.currentTimeMillis() + jwtProperties.getSoftTokenExpirationMs()))
                    .signWith(SignatureAlgorithm.HS256, jwtProperties.getSoftTokenSecret())
                    .compact();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public String getUserIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSoftTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getAmountFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSoftTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.get("amount", Long.class);
    }

    public String getPinFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSoftTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.get("pin").toString();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSoftTokenSecret()).parseClaimsJws(authToken);
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