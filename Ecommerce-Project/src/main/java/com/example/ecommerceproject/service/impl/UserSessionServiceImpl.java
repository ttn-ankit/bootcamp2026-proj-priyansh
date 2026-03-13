package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerceproject.config.TokenBlacklist;
import com.example.ecommerceproject.entity.RefreshToken;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.repository.RefreshTokenRepository;
import com.example.ecommerceproject.service.UserSessionService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class UserSessionServiceImpl implements UserSessionService {

    final RefreshTokenRepository refreshTokenRepository;
    final TokenBlacklist tokenBlacklist;

    @Override
    @Transactional
    public int revokeAllRefreshTokens(User user) {
        if (user == null) {
            return 0;
        }

        List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUser(user);
        
        if (activeTokens.isEmpty()) {
            return 0;
        }

        // Blacklist all associated access tokens and revoke refresh tokens
        activeTokens.forEach(token -> {
            // Blacklist the access token
            if (token.getAccessTokenJti() != null && !token.isRevoked()) {
                long accessTokenExpiryMillis = token.getAccessTokenExpiry()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
                tokenBlacklist.add(token.getAccessTokenJti(), accessTokenExpiryMillis);
            }
            // Mark refresh token as revoked
            token.setRevoked(true);
        });
        
        refreshTokenRepository.saveAll(activeTokens);

        return activeTokens.size();
    }
}