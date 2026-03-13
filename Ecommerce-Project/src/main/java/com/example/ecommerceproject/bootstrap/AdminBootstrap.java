package com.example.ecommerceproject.bootstrap;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.ecommerceproject.constants.MessageKeys;
import com.example.ecommerceproject.entity.Role;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.entity.UserRole;
import com.example.ecommerceproject.entity.UserRoleId;
import com.example.ecommerceproject.enums.RoleEnums;
import com.example.ecommerceproject.repository.RoleRepository;
import com.example.ecommerceproject.repository.UserRepository;
import com.example.ecommerceproject.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if(userRepository.existsByEmailIgnoreCase(MessageKeys.PROTECTED_ADMIN_EMAIL)){
            return;
        }

        User admin = new User();

        admin.setEmail(MessageKeys.PROTECTED_ADMIN_EMAIL);
        admin.setFirstName(MessageKeys.PROTECTED_ADMIN_FIRST_NAME);
        admin.setLastName(MessageKeys.PROTECTED_ADMIN_LAST_NAME);
        admin.setPasswordHash(passwordEncoder.encode(MessageKeys.PROTECTED_ADMIN_PASSWORD));
        admin.setPasswordUpdateDate(LocalDateTime.now());

        admin.setActive(true);
        admin.setLocked(false);
        admin.setExpired(false);
        admin.setInvalidAttemptCount(0);

        userRepository.save(admin);

        Role role = roleRepository.findByAuthority(RoleEnums.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role missing"));

        UserRole userRole = new UserRole(
                new UserRoleId(admin.getId(), role.getId()),
                admin,
                role
        );

        userRoleRepository.save(userRole);
    }
}