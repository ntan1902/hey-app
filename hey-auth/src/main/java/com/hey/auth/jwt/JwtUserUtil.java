package com.hey.auth.jwt;

import com.hey.auth.entity.User;
import com.hey.auth.properties.JwtProperties;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class JwtUserUtil {
    private final JwtProperties jwtProperties;


    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        );

        return doGenerateAccessToken(claims, user.getId());
    }

    public String doGenerateAccessToken(Map<String, Object> claims, String subject) {
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpirationMs()))
                    .signWith(SignatureAlgorithm.HS256, jwtProperties.getUserSecret())
                    .compact();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public String generateRefreshToken(User user) {
        return doGenerateRefreshToken(user.getId());
    }

    private String doGenerateRefreshToken(String subject) {
        try {
            return Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpirationMs()))
                    .signWith(SignatureAlgorithm.HS256, jwtProperties.getUserSecret())
                    .compact();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public String getUserIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getUserSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public List<String> getRolesFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getUserSecret())
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roles", List.class);
    }

    public Date getExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getUserSecret())
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getUserSecret()).parseClaimsJws(authToken);
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
