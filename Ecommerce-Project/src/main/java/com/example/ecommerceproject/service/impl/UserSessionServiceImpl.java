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

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);

        if (tokens.isEmpty()) {
            return 0;
        }

        tokens.forEach(token -> {

            if (token.getAccessTokenJti() != null) {

                long accessExpiryMillis =
                        token.getAccessTokenExpiry()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli();

                tokenBlacklist.add(token.getAccessTokenJti(), accessExpiryMillis);
            }
        });

        refreshTokenRepository.deleteAll(tokens);

        return tokens.size();
    }

    @Override
    @Transactional
    public void deleteRefreshToken(String refreshId) {

        refreshTokenRepository.findByTokenId(refreshId)
                .ifPresent(token -> {

                    if (token.getAccessTokenJti() != null) {

                        long accessExpiryMillis =
                                token.getAccessTokenExpiry()
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli();

                        tokenBlacklist.add(token.getAccessTokenJti(), accessExpiryMillis);
                    }

                    refreshTokenRepository.delete(token);
                });
    }
}