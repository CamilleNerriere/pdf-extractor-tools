package com.noesis.pdf_extractor_tools.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.noesis.pdf_extractor_tools.config.JwtProperties;
import com.noesis.pdf_extractor_tools.exception.JwtValidationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final String secretKey;
    private final long expirationMs;
    private final SecretKeySpec key;

    public JwtService(JwtProperties jwtProperties) {
        this.expirationMs = jwtProperties.getExpiration();
        this.secretKey = jwtProperties.getSecret();
        byte[] bytes = this.secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(bytes, "HmacSHA256");
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key).compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = extractAllClaims(token);
            String username = claims.getSubject();
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(claims));
        } catch (JwtValidationException e) {
            logger.error("Invalid token from user {}", userDetails.getUsername());
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SecurityException | io.jsonwebtoken.MalformedJwtException e) {
            throw new JwtValidationException("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            throw new JwtValidationException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            throw new JwtValidationException("JWT claims string is empty");
        }
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

}
