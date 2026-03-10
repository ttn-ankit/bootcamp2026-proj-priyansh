package com.example.ecommerceproject.bootstrap;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

<<<<<<< HEAD
import com.example.ecommerceproject.constants.MessageKeys;
=======
>>>>>>> bdb0356 (Refactored)
import com.example.ecommerceproject.entity.Role;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.entity.UserRole;
import com.example.ecommerceproject.entity.UserRoleId;
import com.example.ecommerceproject.enums.RoleEnums;
import com.example.ecommerceproject.repository.RoleRepository;
import com.example.ecommerceproject.repository.UserRepository;
import com.example.ecommerceproject.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AdminBootstrap implements CommandLineRunner {

    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final UserRoleRepository userRoleRepository;
    final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

<<<<<<< HEAD
        if(userRepository.existsByEmailIgnoreCase(MessageKeys.PROTECTED_ADMIN_EMAIL)){
=======
        if(userRepository.existsByEmailIgnoreCase("admin@ecommerce.com")){
>>>>>>> bdb0356 (Refactored)
            return;
        }

        User admin = new User();

<<<<<<< HEAD
        admin.setEmail(MessageKeys.PROTECTED_ADMIN_EMAIL);
        admin.setFirstName(MessageKeys.PROTECTED_ADMIN_FIRST_NAME);
        admin.setLastName(MessageKeys.PROTECTED_ADMIN_LAST_NAME);
        admin.setPasswordHash(passwordEncoder.encode(MessageKeys.PROTECTED_ADMIN_PASSWORD));
=======
        admin.setEmail("admin@ecommerce.com");
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
>>>>>>> bdb0356 (Refactored)
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
<<<<<<< HEAD
=======

        System.out.println("Default admin created.");
>>>>>>> bdb0356 (Refactored)
    }
}