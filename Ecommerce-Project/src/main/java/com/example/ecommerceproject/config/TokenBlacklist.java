package com.example.ecommerceproject.config;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = PRIVATE)
public class TokenBlacklist {

    final Map<String, Long> blacklist = new ConcurrentHashMap<>();

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
