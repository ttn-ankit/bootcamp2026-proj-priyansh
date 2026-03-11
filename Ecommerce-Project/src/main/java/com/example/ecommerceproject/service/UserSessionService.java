package com.example.ecommerceproject.service;

import com.example.ecommerceproject.entity.User;

public interface UserSessionService {
    int revokeAllRefreshTokens(User user);
}