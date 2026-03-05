package com.example.ecommerceproject.service.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.repository.UserRepository;
import com.example.ecommerceproject.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService, CustomUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean credentialsNonExpired = user.getPasswordUpdateDate()
                .plusDays(90)
                .isAfter(LocalDateTime.now());

        List<SimpleGrantedAuthority> authorities = user.getUserRoles()
                .stream()
                .map(ur -> new SimpleGrantedAuthority(
                        ur.getRole().getAuthority().name()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive(),
                !user.isExpired(),
                credentialsNonExpired,
                !user.isLocked(),
                authorities);
    }
}
