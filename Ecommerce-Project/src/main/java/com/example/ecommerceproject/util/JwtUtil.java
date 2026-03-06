package com.example.ecommerceproject.util;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

        private static final long ACCESS_TOKEN_VALIDITY = 86400000;
        private static final long PASSWORD_RESET_TOKEN_VALIDITY = 3600000;
        private static final int HMAC_KEY_MIN_BYTES = 32;
        private static final String CLAIM_PURPOSE = "purpose";
        private static final String PURPOSE_PASSWORD_RESET = "password_reset";
        private static final String CLAIM_PASSWORD_UPDATED_AT = "pwdUpdatedAt";

        private final SecretKey key;

        public JwtUtil(@Value("${jwt.secret.key}") String secretKey) {
                byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
                if (keyBytes.length < HMAC_KEY_MIN_BYTES) {
                        throw new IllegalArgumentException("jwt.secret.key must be at least 32 bytes for HS256");
                }
                this.key = Keys.hmacShaKeyFor(keyBytes);
        }

        public String generateToken(
                        Long userId,
                        String email,
                        Collection<? extends GrantedAuthority> roles) {

                List<String> roleNames = roles.stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList();

                return Jwts.builder()
                                .setId(UUID.randomUUID().toString())
                                .setSubject(email)
                                .claim("userId", userId)
                                .claim("roles", roleNames)
                                .setIssuedAt(new Date())
                                .setExpiration(
                                                new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                                .signWith(key)
                                .compact();
        }

        public String generatePasswordResetToken(Long userId, String email, long passwordUpdatedAtMillis) {
                return Jwts.builder()
                                .setId(UUID.randomUUID().toString())
                                .setSubject(email)
                                .claim("userId", userId)
                                .claim(CLAIM_PURPOSE, PURPOSE_PASSWORD_RESET)
                                .claim(CLAIM_PASSWORD_UPDATED_AT, passwordUpdatedAtMillis)
                                .setIssuedAt(new Date())
                                .setExpiration(new Date(System.currentTimeMillis() + PASSWORD_RESET_TOKEN_VALIDITY))
                                .signWith(key)
                                .compact();
        }

        public boolean isPasswordResetTokenValid(String token) {
                try {
                        Claims claims = extractAllClaims(token);
                        String purpose = claims.get(CLAIM_PURPOSE, String.class);
                        if (!PURPOSE_PASSWORD_RESET.equals(purpose)) {
                                return false;
                        }
                        return claims.getExpiration().after(new Date());
                } catch (Exception e) {
                        return false;
                }
        }

        public String extractJti(String token) {
                return extractAllClaims(token).getId();
        }

        public String extractEmail(String token) {
                return extractAllClaims(token).getSubject();
        }

        public Claims extractAllClaims(String token) {

                return Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
        }

        public Long extractUserId(String token) {

                return extractAllClaims(token)
                                .get("userId", Long.class);
        }

        public boolean isTokenExpired(String token) {

                return extractAllClaims(token)
                                .getExpiration()
                                .before(new Date());
        }

        public boolean validateToken(String token, UserDetails user) {

                Claims claim = extractAllClaims(token);
                String email = claim.getSubject();
                Date expiration = claim.getExpiration();

                return email.equals(user.getUsername()) && expiration.after(new Date());
        }
        
        public boolean isTokenValid(String token) {
                try {
                        Claims claims = extractAllClaims(token);
                        return claims.getExpiration().after(new Date());
                } catch (Exception e) {
                        return false;
                }
        }
}