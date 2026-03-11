package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // Mark all tokens as revoked
        activeTokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(activeTokens);

        return activeTokens.size();
    }
}