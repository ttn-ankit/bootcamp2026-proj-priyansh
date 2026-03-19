package com.example.ecommerceproject.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.AccessToken;
import com.example.ecommerceproject.entity.User;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    Optional<AccessToken> findByJti(String jti);

    @Query("SELECT COUNT(a) > 0 FROM AccessToken a WHERE a.jti = :jti AND a.expiryDate > :now")
    boolean existsByJtiAndNotExpired(@Param("jti") String jti, @Param("now") LocalDateTime now);

    void deleteByUser(User user);

    @Modifying
    @Query("DELETE FROM AccessToken a WHERE a.expiryDate < :cutoffDate")
    int deleteExpiredTokens(@Param("cutoffDate") LocalDateTime cutoffDate);
}