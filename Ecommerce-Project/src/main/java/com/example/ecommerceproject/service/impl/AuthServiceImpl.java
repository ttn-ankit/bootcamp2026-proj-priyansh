package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.dto.AddressDTO;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.ForgotPasswordRequestDTO;
import com.example.ecommerceproject.dto.LoginRequestDTO;
import com.example.ecommerceproject.dto.LoginResponseDTO;
import com.example.ecommerceproject.dto.RegisterRequestDTO;
import com.example.ecommerceproject.dto.ResetPasswordRequestDTO;
import com.example.ecommerceproject.dto.SellerRegisterRequestDTO;
import com.example.ecommerceproject.entity.*;
import com.example.ecommerceproject.enums.RoleEnums;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.config.TokenBlacklist;
import com.example.ecommerceproject.repository.*;
import com.example.ecommerceproject.service.AuthService;
import com.example.ecommerceproject.service.EmailService;
import com.example.ecommerceproject.util.JwtUtil;
import com.example.ecommerceproject.util.MessageService;
import com.example.ecommerceproject.constants.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AuthServiceImpl implements AuthService {
    static final int MAX_FAILED_ATTEMPTS = 3;

    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final UserRoleRepository userRoleRepository;
    final CustomerRepository customerRepository;
    final SellerRepository sellerRepository;
    final AddressRepository addressRepository;
    final ActivationTokenRepository activationTokenRepository;
    final PasswordEncoder passwordEncoder;
    final EmailService emailService;
    final AuthenticationManager authenticationManager;
    final JwtUtil jwtUtil;
    final RefreshTokenRepository refreshTokenRepository;
    final TokenBlacklist tokenBlacklist;
    final MessageService messageService;

    @Override
    @Transactional
    public ApiResponseDTO register(RegisterRequestDTO dto) {

        validateCustomerRegistration(dto);

        User user = createUser(dto);

        assignRole(user, RoleEnums.ROLE_CUSTOMER);

        createCustomer(user, dto.getPhoneNumber());

        createActivationToken(user);

        return new ApiResponseDTO(messageService.getMessage(MessageKeys.AUTH_REGISTRATION_SUCCESS));
    }

    @Override
    @Transactional
    public ApiResponseDTO registerSeller(SellerRegisterRequestDTO dto) {

        validateSellerRegistration(dto);

        User user = createUser(dto);

        assignRole(user, RoleEnums.ROLE_SELLER);

        createSeller(user, dto);

        saveAddress(user, dto.getAddress());

        emailService.sendSellerRegistrationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.getMessage(MessageKeys.AUTH_SELLER_REGISTRATION_SUCCESS));
    }

    @Override
    @Transactional(noRollbackFor = ApiException.class)
    public ApiResponseDTO activateAccount(String tokenValue) {

        ActivationToken token = activationTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_INVALID_ACTIVATION_TOKEN, 400));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            User user = token.getUser();
            activationTokenRepository.delete(token);
            createActivationToken(user);
            throw new ApiException(MessageKeys.AUTH_ACTIVATION_EXPIRED, 400);
        }

        User user = token.getUser();
        user.setActive(true);
        userRepository.save(user);
        activationTokenRepository.delete(token);

        return new ApiResponseDTO(messageService.getMessage(MessageKeys.AUTH_ACTIVATION_SUCCESS));
    }

    @Override
    @Transactional
    public ApiResponseDTO resendActivationLink(String email) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 400));

        if (user.isActive()) {
            throw new ApiException(MessageKeys.AUTH_ACCOUNT_ALREADY_ACTIVATED, 400);
        }

        activationTokenRepository.deleteByUser(user);
        createActivationToken(user);

        return new ApiResponseDTO(messageService.getMessage(MessageKeys.AUTH_RESEND_ACTIVATION_SUCCESS));
    }

    @Override
    @Transactional
    public ApiResponseDTO approveSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ApiException(MessageKeys.ERROR_SELLER_NOT_FOUND, 400));

        seller.setApproved(true);
        User user = seller.getUser();
        user.setActive(true);
        sellerRepository.save(seller);
        userRepository.save(user);

        return new ApiResponseDTO(messageService.getMessage(MessageKeys.AUTH_SELLER_APPROVED));
    }

    @Override
    @Transactional
    public ApiResponseDTO rejectSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ApiException(MessageKeys.ERROR_SELLER_NOT_FOUND, 400));

        User user = seller.getUser();
        if (isProtectedAdmin(user)) {
            throw new ApiException(MessageKeys.AUTH_ADMIN_PROTECTED, 400);
        }
        user.setDeleted(true);
        sellerRepository.delete(seller);
        userRepository.save(user);

        return new ApiResponseDTO(messageService.getMessage(MessageKeys.AUTH_SELLER_REJECTED));
    }

    private void validateCustomerRegistration(RegisterRequestDTO dto) {

        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ApiException(MessageKeys.VALIDATION_EMAIL_EXISTS, 400);
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ApiException(MessageKeys.VALIDATION_PASSWORDS_DO_NOT_MATCH, 400);
        }
    }

    private void validateSellerRegistration(SellerRegisterRequestDTO dto) {
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new ApiException(MessageKeys.VALIDATION_EMAIL_EXISTS, 400);
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ApiException(MessageKeys.VALIDATION_PASSWORDS_DO_NOT_MATCH, 400);
        }
        if (sellerRepository.existsByGstIgnoreCase(dto.getGst())) {
            throw new ApiException(MessageKeys.VALIDATION_GST_EXISTS, 400);
        }
        if (sellerRepository.existsByCompanyNameIgnoreCase(dto.getCompanyName())) {
            throw new ApiException(MessageKeys.VALIDATION_COMPANY_NAME_EXISTS, 400);
        }
    }

    @Override
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            User entity = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 400));
            entity.setInvalidAttemptCount(0);
            userRepository.save(entity);

            String accessToken = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getAuthorities());

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(entity);
            String refreshId = UUID.randomUUID().toString();
            refreshToken.setTokenId(refreshId);
            refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));
            refreshTokenRepository.save(refreshToken);

            String refreshTokenValue = jwtUtil.generateRefreshToken(user.getUserId(), user.getUsername(), refreshId);

            return new LoginResponseDTO(
                    accessToken,
                    refreshTokenValue,
                    user.getAuthorities().stream().toList(),
                    user.getUsername(),
                    messageService.getMessage(MessageKeys.AUTH_LOGIN_SUCCESS));
        } catch (BadCredentialsException e) {
            userRepository.findByEmailAndIsDeletedFalse(dto.getEmail()).ifPresent(user -> {
                if (isProtectedAdmin(user)) {
                    return;
                }
                int newCount = (user.getInvalidAttemptCount() == null ? 0 : user.getInvalidAttemptCount()) + 1;
                user.setInvalidAttemptCount(newCount);
                if (newCount >= MAX_FAILED_ATTEMPTS) {
                    user.setLocked(true);
                    emailService.sendAccountLockedEmail(user.getEmail());
                }
                userRepository.save(user);
            });
            throw new ApiException(MessageKeys.AUTH_INVALID_CREDENTIALS, 401);
        }
    }

    @Override
    @Transactional
    public ApiResponseDTO logout(String accessTokenValue, String refreshTokenValue) {
        boolean accessTokenHandled = false;
        boolean refreshTokenHandled = false;

        if (accessTokenValue != null && !accessTokenValue.isBlank()) {
            if (jwtUtil.isTokenValid(accessTokenValue)) {
                Claims claims = jwtUtil.extractAllClaims(accessTokenValue);
                tokenBlacklist.add(claims.getId(), claims.getExpiration().getTime());
                accessTokenHandled = true;
            }
        }

        if (refreshTokenValue != null && !refreshTokenValue.isBlank()) {
            if (jwtUtil.isRefreshTokenValid(refreshTokenValue)) {
                String refreshId = jwtUtil.extractRefreshId(refreshTokenValue);
                if (refreshId != null) {
                    refreshTokenRepository.findByTokenIdAndRevokedFalse(refreshId).ifPresent(storedToken -> {
                        storedToken.setRevoked(true);
                        refreshTokenRepository.save(storedToken);
                    });
                    refreshTokenHandled = true;
                }
            }
        }

        if (!accessTokenHandled && !refreshTokenHandled) {
            throw new ApiException(MessageKeys.AUTH_TOKEN_REQUIRED, 403);
        }

        return new ApiResponseDTO(messageService.getMessage(MessageKeys.AUTH_LOGOUT_SUCCESS));
    }

    @Override
    @Transactional
    public LoginResponseDTO refreshAccessToken(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            throw new ApiException(MessageKeys.AUTH_TOKEN_REQUIRED, 403);
        }
        if (!jwtUtil.isRefreshTokenValid(refreshTokenValue)) {
            throw new ApiException(MessageKeys.AUTH_INVALID_REFRESH_TOKEN, 403);
        }

        Claims claims = jwtUtil.extractAllClaims(refreshTokenValue);
        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();
        String refreshId = jwtUtil.extractRefreshId(refreshTokenValue);

        if (userId == null || email == null || refreshId == null) {
            throw new ApiException(MessageKeys.AUTH_INVALID_REFRESH_TOKEN, 403);
        }

        RefreshToken existingToken = refreshTokenRepository.findByTokenIdAndRevokedFalse(refreshId)
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_REFRESH_TOKEN_REVOKED, 403));

        if (existingToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            existingToken.setRevoked(true);
            refreshTokenRepository.save(existingToken);
            throw new ApiException(MessageKeys.AUTH_REFRESH_TOKEN_EXPIRED, 403);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 400));

        if (!email.equalsIgnoreCase(user.getEmail())) {
            throw new ApiException(MessageKeys.AUTH_INVALID_REFRESH_TOKEN, 403);
        }

        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setUser(user);
        String newRefreshId = UUID.randomUUID().toString();
        newRefreshToken.setTokenId(newRefreshId);
        newRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        refreshTokenRepository.save(newRefreshToken);

        String newRefreshTokenValue = jwtUtil.generateRefreshToken(user.getId(), user.getEmail(), newRefreshId);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtUtil.generateToken(user.getId(), user.getEmail(), userDetails.getAuthorities());

        return new LoginResponseDTO(
                newAccessToken,
                newRefreshTokenValue,
                userDetails.getAuthorities().stream().toList(),
                user.getEmail(),
                messageService.getMessage(MessageKeys.AUTH_REFRESH_SUCCESS));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseDTO requestPasswordReset(ForgotPasswordRequestDTO dto) {
        User user = userRepository.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 400));

        if (!user.isActive()) {
            throw new ApiException(MessageKeys.VALIDATION_ACCOUNT_NOT_ACTIVATED, 400);
        }

        long pwdUpdatedAtMillis = user.getPasswordUpdateDate() == null
                ? 0L
                : user.getPasswordUpdateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        String token = jwtUtil.generatePasswordResetToken(user.getId(), user.getEmail(), pwdUpdatedAtMillis);
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return new ApiResponseDTO(messageService.getMessage(MessageKeys.AUTH_PASSWORD_RESET_SENT));
    }

    @Override
    @Transactional
    public ApiResponseDTO resetPassword(ResetPasswordRequestDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ApiException(MessageKeys.VALIDATION_PASSWORDS_DO_NOT_MATCH, 400);
        }

        String token = dto.getToken();
        if (!jwtUtil.isPasswordResetTokenValid(token)) {
            throw new ApiException(MessageKeys.VALIDATION_INVALID_RESET_TOKEN, 400);
        }

        Claims claims = jwtUtil.extractAllClaims(token);
        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();
        Long tokenPwdUpdatedAt = claims.get("pwdUpdatedAt", Long.class);
        if (userId == null || email == null || tokenPwdUpdatedAt == null) {
            throw new ApiException(MessageKeys.VALIDATION_INVALID_RESET_TOKEN, 400);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 400));

        if (!email.equalsIgnoreCase(user.getEmail())) {
            throw new ApiException(MessageKeys.VALIDATION_INVALID_RESET_TOKEN, 400);
        }
        if (!user.isActive()) {
            throw new ApiException(MessageKeys.VALIDATION_ACCOUNT_NOT_ACTIVATED, 400);
        }

        long currentPwdUpdatedAtMillis = user.getPasswordUpdateDate() == null
                ? 0L
                : user.getPasswordUpdateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if (currentPwdUpdatedAtMillis != tokenPwdUpdatedAt.longValue()) {
            throw new ApiException(MessageKeys.VALIDATION_RESET_TOKEN_USED, 400);
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendPasswordChangedEmail(user.getEmail());
        return new ApiResponseDTO(messageService.getMessage(MessageKeys.AUTH_PASSWORD_UPDATED));
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
                .orElseThrow(() -> new ApiException(MessageKeys.ERROR_ROLE_NOT_FOUND, 400));

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

    private void saveAddress(User user, AddressDTO dto) {

        Address address = new Address();

        address.setAddressLine(dto.getAddressLine());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());
        address.setLabel(dto.getLabel());
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

    private boolean isProtectedAdmin(User user) {
        return user != null && MessageKeys.PROTECTED_ADMIN_EMAIL.equalsIgnoreCase(user.getEmail());
    }
}