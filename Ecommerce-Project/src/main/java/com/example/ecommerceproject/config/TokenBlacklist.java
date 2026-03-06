package com.example.ecommerceproject.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * In-memory blacklist of revoked JWT IDs (jti). Tokens added on logout cannot be used again.
 * Entries are removed after the token would have expired.
 */
@Component
public class TokenBlacklist {

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void add(String jti, long expiresAtMillis) {
        blacklist.put(jti, expiresAtMillis);
    }

    public boolean contains(String jti) {
        Long expiresAt = blacklist.get(jti);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt <= System.currentTimeMillis()) {
            blacklist.remove(jti);
            return false;
        }
        return true;
    }

    @Scheduled(fixedRate = 300_000)
    public void removeExpired() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() <= now);
    }
}
