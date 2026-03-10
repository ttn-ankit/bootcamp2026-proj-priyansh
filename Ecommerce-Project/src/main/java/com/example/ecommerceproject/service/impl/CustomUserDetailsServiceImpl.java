package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ecommerceproject.entity.User;
<<<<<<< HEAD
import com.example.ecommerceproject.exception.ApiException;
=======
>>>>>>> bdb0356 (Refactored)
import com.example.ecommerceproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findWithRolesByEmailAndIsDeletedFalse(email)
                .orElseThrow(() ->
<<<<<<< HEAD
                        new ApiException("User not found", 400));
=======
                        new UsernameNotFoundException("User not found"));
>>>>>>> bdb0356 (Refactored)

        return new CustomUserDetails(user);
    }
}