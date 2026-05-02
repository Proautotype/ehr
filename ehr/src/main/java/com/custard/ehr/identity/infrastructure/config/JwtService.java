package com.custard.ehr.identity.infrastructure;

import com.custard.ehr.identity.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationMinutes;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-expiration-minutes:60}") long accessTokenExpirationMinutes,
            @Value("${security.jwt.refresh-token-expiration-minutes:10080}") long refreshTokenExpirationMinutes
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationMinutes = refreshTokenExpirationMinutes;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, "ACCESS", accessTokenExpirationMinutes);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, "REFRESH", refreshTokenExpirationMinutes);
    }

    private String generateToken(User user, String type, long expirationMinutes) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .claim("fullName", user.getFullName())
                .claim("type", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isAccessTokenValid(String token, String username) {
        Claims claims = extractClaims(token);

        return claims.getSubject().equals(username)
                && "ACCESS".equals(claims.get("type", String.class))
                && claims.getExpiration().after(new Date());
    }

    public boolean isRefreshTokenValid(String token) {
        Claims claims = extractClaims(token);

        return "REFRESH".equals(claims.get("type", String.class))
                && claims.getExpiration().after(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}