package com.hey.authentication.jwt;

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
public class JwtUserUtil {
    @Value("${JWT_USER_SECRET}")
    private String JWT_SECRET;

    @Value("${JWT_EXPIRATION_MS}")
    private Long JWT_EXPIRATION;


    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        Collection<? extends GrantedAuthority> roles = user.getAuthorities();

        if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            claims.put("isAdmin", true);
        }

        if (roles.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            claims.put("isUser", true);
        }

        return doGenerateToken(claims, user.getId().toString());
    }

    public String doGenerateToken(Map<String, Object> claims, String subject) {
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                    .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                    .compact();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public Long getUserIdFromJwt(String token) {
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
