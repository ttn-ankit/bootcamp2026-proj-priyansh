package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.ForgotPasswordRequestDTO;
import com.example.ecommerceproject.dto.LoginRequestDTO;
import com.example.ecommerceproject.dto.LoginResponseDTO;
import com.example.ecommerceproject.dto.RegisterRequestDTO;
import com.example.ecommerceproject.dto.ResetPasswordRequestDTO;
import com.example.ecommerceproject.dto.SellerRegisterRequestDTO;
import com.example.ecommerceproject.entity.*;
import com.example.ecommerceproject.enums.AddressLabelEnums;
import com.example.ecommerceproject.enums.RoleEnums;
import com.example.ecommerceproject.config.TokenBlacklist;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.exception.BadRequestException;
import com.example.ecommerceproject.exception.DuplicateResourceException;
import com.example.ecommerceproject.exception.InvalidTokenException;
import com.example.ecommerceproject.exception.ResourceNotFoundException;
import com.example.ecommerceproject.repository.*;
import com.example.ecommerceproject.service.AuthService;
import com.example.ecommerceproject.service.EmailService;
import com.example.ecommerceproject.util.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 3;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final AddressRepository addressRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    @Override
    public ApiResponseDTO register(RegisterRequestDTO dto) {

        validateCustomerRegistration(dto);

        User user = createUser(dto);

        assignRole(user, RoleEnums.ROLE_CUSTOMER);

        createCustomer(user, dto.getPhoneNumber());

        saveAddress(user, dto.getAddressLine(), dto.getCity(), dto.getState(),
                dto.getCountry(), dto.getZipCode(), dto.getLabel());

        createActivationToken(user);

        return new ApiResponseDTO(
                "Registration successful. Please check email for activation link");
    }

    @Override
    public ApiResponseDTO registerSeller(SellerRegisterRequestDTO dto) {

        validateSellerRegistration(dto);

        User user = createUser(dto);

        assignRole(user, RoleEnums.ROLE_SELLER);

        createSeller(user, dto);

        saveAddress(user, dto.getAddressLine(), dto.getCity(), dto.getState(),
                dto.getCountry(), dto.getZipCode(), dto.getLabel());

        emailService.sendSellerRegistrationEmail(user.getEmail());

        return new ApiResponseDTO(
                "Seller registered successfully. Waiting for admin approval.");
    }

    @Override
    public ApiResponseDTO activateAccount(String tokenValue) {

        ActivationToken token = activationTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() -> new InvalidTokenException("Invalid activation token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {

            User user = token.getUser();

            activationTokenRepository.delete(token);

            createActivationToken(user);

            throw new InvalidTokenException(
                    "Activation token expired. A new activation link has been sent.");
        }

        User user = token.getUser();

        user.setActive(true);

        userRepository.save(user);

        activationTokenRepository.delete(token);

        return new ApiResponseDTO("Account successfully activated");
    }

    @Override
    public ApiResponseDTO resendActivationLink(String email) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isActive()) {
            throw new BadRequestException("Account already activated");
        }

        activationTokenRepository.deleteByUser(user);

        createActivationToken(user);

        return new ApiResponseDTO("A new activation link has been sent");
    }

    @Override
    public ApiResponseDTO approveSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        seller.setApproved(true);

        User user = seller.getUser();
        user.setActive(true);

        sellerRepository.save(seller);
        userRepository.save(user);

        return new ApiResponseDTO("Seller approved successfully");
    }

    @Override
    public ApiResponseDTO rejectSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        User user = seller.getUser();

        user.setDeleted(true);

        sellerRepository.delete(seller);
        userRepository.save(user);

        return new ApiResponseDTO("Seller rejected successfully");
    }

    private void validateCustomerRegistration(RegisterRequestDTO dto) {

        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
    }

    private void validateSellerRegistration(SellerRegisterRequestDTO dto) {

        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (sellerRepository.existsByGstIgnoreCase(dto.getGst())) {
            throw new DuplicateResourceException("GST already registered");
        }
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            User entity = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            entity.setInvalidAttemptCount(0);
            userRepository.save(entity);

            String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getAuthorities());
            return new LoginResponseDTO(
                    token,
                    user.getAuthorities().stream().toList(),
                    user.getUsername(),
                    "Login Successfull!");
        } catch (BadCredentialsException e) {
            userRepository.findByEmailAndIsDeletedFalse(dto.getEmail()).ifPresent(user -> {
                int newCount = (user.getInvalidAttemptCount() == null ? 0 : user.getInvalidAttemptCount()) + 1;
                user.setInvalidAttemptCount(newCount);
                if (newCount >= MAX_FAILED_ATTEMPTS) {
                    user.setLocked(true);
                    emailService.sendAccountLockedEmail(user.getEmail());
                }
                userRepository.save(user);
            });
            throw new BadCredentialsException("Invalid email or password.");
        }
    }

    @Override
    public ApiResponseDTO logout(String token) {
        if (token == null || token.isBlank()) {
            throw new ApiException("Access token is required", HttpStatus.UNAUTHORIZED);
        }
        if (!jwtUtil.isTokenValid(token)) {
            throw new ApiException("Invalid or expired access token", HttpStatus.UNAUTHORIZED);
        }
        Claims claims = jwtUtil.extractAllClaims(token);
        tokenBlacklist.add(claims.getId(), claims.getExpiration().getTime());
        return new ApiResponseDTO("Logged out successfully");
    }

    @Override
    public ApiResponseDTO requestPasswordReset(ForgotPasswordRequestDTO dto) {
        User user = userRepository.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new BadRequestException("Account is not activated");
        }

        long pwdUpdatedAtMillis = user.getPasswordUpdateDate() == null
                ? 0L
                : user.getPasswordUpdateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        String token = jwtUtil.generatePasswordResetToken(user.getId(), user.getEmail(), pwdUpdatedAtMillis);
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return new ApiResponseDTO("Password reset link has been sent to your email");
    }

    @Override
    public ApiResponseDTO resetPassword(ResetPasswordRequestDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        String token = dto.getToken();
        if (!jwtUtil.isPasswordResetTokenValid(token)) {
            throw new InvalidTokenException("Invalid or expired reset token");
        }

        Claims claims = jwtUtil.extractAllClaims(token);
        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();
        Long tokenPwdUpdatedAt = claims.get("pwdUpdatedAt", Long.class);
        if (userId == null || email == null || tokenPwdUpdatedAt == null) {
            throw new InvalidTokenException("Invalid reset token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!email.equalsIgnoreCase(user.getEmail())) {
            throw new InvalidTokenException("Invalid reset token");
        }
        if (!user.isActive()) {
            throw new BadRequestException("Account is not activated");
        }

        long currentPwdUpdatedAtMillis = user.getPasswordUpdateDate() == null
                ? 0L
                : user.getPasswordUpdateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if (currentPwdUpdatedAtMillis != tokenPwdUpdatedAt.longValue()) {
            throw new InvalidTokenException("Reset token already used or invalid");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendPasswordChangedEmail(user.getEmail());
        return new ApiResponseDTO("Password successfully updated");
    }

    private User createUser(RegisterRequestDTO dto) {

        User user = new User();

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());

        user.setActive(false);
        user.setExpired(false);
        user.setLocked(false);
        user.setDeleted(false);
        user.setInvalidAttemptCount(0);

        return userRepository.save(user);
    }

    private User createUser(SellerRegisterRequestDTO dto) {

        User user = new User();

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        user.setActive(false);
        user.setExpired(false);
        user.setLocked(false);
        user.setDeleted(false);
        user.setInvalidAttemptCount(0);

        return userRepository.save(user);
    }

    private void assignRole(User user, RoleEnums roleEnum) {

        Role role = roleRepository.findByAuthority(roleEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        UserRole userRole = new UserRole(
                new UserRoleId(user.getId(), role.getId()),
                user,
                role);

        userRoleRepository.save(userRole);
    }

    private void createCustomer(User user, String contact) {

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setContact(contact);

        customerRepository.save(customer);
    }

    private void createSeller(User user, SellerRegisterRequestDTO dto) {

        Seller seller = new Seller();

        seller.setUser(user);
        seller.setCompanyName(dto.getCompanyName());
        seller.setCompanyContact(dto.getCompanyContact());
        seller.setGst(dto.getGst());
        seller.setApproved(false);

        sellerRepository.save(seller);
    }

    private void saveAddress(User user, String addressLine, String city,
            String state, String country, String zipCode,
            AddressLabelEnums label) {

        Address address = new Address();

        address.setAddressLine(addressLine);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setZipCode(zipCode);
        address.setLabel(label);
        address.setUser(user);

        addressRepository.save(address);
    }

    private void createActivationToken(User user) {

        String tokenValue = UUID.randomUUID().toString();

        ActivationToken token = new ActivationToken();

        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(3));
        token.setUsed(false);

        activationTokenRepository.save(token);

        emailService.sendActivationEmail(user.getEmail(), tokenValue);
    }
}