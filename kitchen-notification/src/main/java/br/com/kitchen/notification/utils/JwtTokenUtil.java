package br.com.kitchen.notification.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        return getAllClaims(token).get("id", Long.class);
    }

    public List<String> getRoles(String token) {
        return getAllClaims(token).get("roles", List.class);
    }

    public boolean isTokenExpired(String token) {
        return getAllClaims(token)
                .getExpiration()
                .before(new Date());
    }
    public boolean IsSellerFromToken(String jwtToken) {
        List<String> roles = getRoles(jwtToken);
        return roles != null && roles.contains("ROLE_SELLER");
    }

    public boolean IsCustomerFromToken(String jwtToken) {
        List<String> roles = getRoles(jwtToken);
        return roles != null && roles.contains("ROLE_USER");
    }
}