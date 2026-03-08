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
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.exception.BadRequestException;
import com.example.ecommerceproject.exception.DuplicateResourceException;
import com.example.ecommerceproject.exception.InvalidTokenException;
import com.example.ecommerceproject.exception.ResourceNotFoundException;
import com.example.ecommerceproject.config.TokenBlacklist;
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
import java.util.Locale;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final String PROTECTED_ADMIN_EMAIL = "admin@ecommerce.com";

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
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklist tokenBlacklist;
    private final MessageSource messageSource;

    @Override
    public ApiResponseDTO register(RegisterRequestDTO dto) {

        validateCustomerRegistration(dto);

        User user = createUser(dto);

        assignRole(user, RoleEnums.ROLE_CUSTOMER);

        createCustomer(user, dto.getPhoneNumber());

        saveAddress(user, dto.getAddressLine(), dto.getCity(), dto.getState(),
                dto.getCountry(), dto.getZipCode(), dto.getLabel());

        createActivationToken(user);

        return new ApiResponseDTO(msg("auth.registration_success"));
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

        return new ApiResponseDTO(msg("auth.seller_registration_success"));
    }

    @Override
    public ApiResponseDTO activateAccount(String tokenValue) {

        ActivationToken token = activationTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() -> new InvalidTokenException("auth.invalid_activation_token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            User user = token.getUser();
            activationTokenRepository.delete(token);
            createActivationToken(user);
            throw new InvalidTokenException("auth.activation_expired");
        }

        User user = token.getUser();
        user.setActive(true);
        userRepository.save(user);
        activationTokenRepository.delete(token);

        return new ApiResponseDTO(msg("auth.activation_success"));
    }

    @Override
    public ApiResponseDTO resendActivationLink(String email) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("auth.user_not_found"));

        if (user.isActive()) {
            throw new BadRequestException("auth.account_already_activated");
        }

        activationTokenRepository.deleteByUser(user);
        createActivationToken(user);

        return new ApiResponseDTO(msg("auth.resend_activation_success"));
    }

    @Override
    public ApiResponseDTO approveSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("error.seller_not_found"));

        seller.setApproved(true);
        User user = seller.getUser();
        user.setActive(true);
        sellerRepository.save(seller);
        userRepository.save(user);

        return new ApiResponseDTO(msg("auth.seller_approved"));
    }

    @Override
    public ApiResponseDTO rejectSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("error.seller_not_found"));

        User user = seller.getUser();
        if (isProtectedAdmin(user)) {
            throw new BadRequestException("auth.admin_protected");
        }
        user.setDeleted(true);
        sellerRepository.delete(seller);
        userRepository.save(user);

        return new ApiResponseDTO(msg("auth.seller_rejected"));
    }

    private void validateCustomerRegistration(RegisterRequestDTO dto) {

        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateResourceException("validation.email_exists");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("validation.passwords_do_not_match");
        }
    }

    private void validateSellerRegistration(SellerRegisterRequestDTO dto) {
        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateResourceException("validation.email_exists");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("validation.passwords_do_not_match");
        }
        if (sellerRepository.existsByGstIgnoreCase(dto.getGst())) {
            throw new DuplicateResourceException("validation.gst_exists");
        }
        if (sellerRepository.existsByCompanyNameIgnoreCase(dto.getCompanyName())) {
            throw new DuplicateResourceException("validation.company_name_exists");
        }
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            User entity = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("auth.user_not_found"));
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
                    msg("auth.login_success"));
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
            throw new BadCredentialsException("Invalid email or password.");
        }
    }

    @Override
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
            throw new ApiException("auth.token_required", HttpStatus.UNAUTHORIZED);
        }

        return new ApiResponseDTO(msg("auth.logout_success"));
    }

    @Override
    public LoginResponseDTO refreshAccessToken(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            throw new ApiException("auth.token_required", HttpStatus.UNAUTHORIZED);
        }
        if (!jwtUtil.isRefreshTokenValid(refreshTokenValue)) {
            throw new ApiException("auth.invalid_refresh_token", HttpStatus.UNAUTHORIZED);
        }

        Claims claims = jwtUtil.extractAllClaims(refreshTokenValue);
        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();
        String refreshId = jwtUtil.extractRefreshId(refreshTokenValue);

        if (userId == null || email == null || refreshId == null) {
            throw new ApiException("auth.invalid_refresh_token", HttpStatus.UNAUTHORIZED);
        }

        RefreshToken existingToken = refreshTokenRepository.findByTokenIdAndRevokedFalse(refreshId)
                .orElseThrow(() -> new ApiException("auth.refresh_token_revoked", HttpStatus.UNAUTHORIZED));

        if (existingToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            existingToken.setRevoked(true);
            refreshTokenRepository.save(existingToken);
            throw new ApiException("auth.refresh_token_expired", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("auth.user_not_found"));

        if (!email.equalsIgnoreCase(user.getEmail())) {
            throw new ApiException("auth.invalid_refresh_token", HttpStatus.UNAUTHORIZED);
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
                msg("auth.refresh_success"));
    }

    @Override
    public ApiResponseDTO requestPasswordReset(ForgotPasswordRequestDTO dto) {
        User user = userRepository.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("auth.user_not_found"));

        if (!user.isActive()) {
            throw new BadRequestException("validation.account_not_activated");
        }

        long pwdUpdatedAtMillis = user.getPasswordUpdateDate() == null
                ? 0L
                : user.getPasswordUpdateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        String token = jwtUtil.generatePasswordResetToken(user.getId(), user.getEmail(), pwdUpdatedAtMillis);
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return new ApiResponseDTO(msg("auth.password_reset_sent"));
    }

    @Override
    public ApiResponseDTO resetPassword(ResetPasswordRequestDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("validation.passwords_do_not_match");
        }

        String token = dto.getToken();
        if (!jwtUtil.isPasswordResetTokenValid(token)) {
            throw new InvalidTokenException("validation.invalid_reset_token");
        }

        Claims claims = jwtUtil.extractAllClaims(token);
        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();
        Long tokenPwdUpdatedAt = claims.get("pwdUpdatedAt", Long.class);
        if (userId == null || email == null || tokenPwdUpdatedAt == null) {
            throw new InvalidTokenException("validation.invalid_reset_token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("auth.user_not_found"));

        if (!email.equalsIgnoreCase(user.getEmail())) {
            throw new InvalidTokenException("validation.invalid_reset_token");
        }
        if (!user.isActive()) {
            throw new BadRequestException("validation.account_not_activated");
        }

        long currentPwdUpdatedAtMillis = user.getPasswordUpdateDate() == null
                ? 0L
                : user.getPasswordUpdateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if (currentPwdUpdatedAtMillis != tokenPwdUpdatedAt.longValue()) {
            throw new InvalidTokenException("validation.reset_token_used");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendPasswordChangedEmail(user.getEmail());
        return new ApiResponseDTO(msg("auth.password_updated"));
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
                .orElseThrow(() -> new ResourceNotFoundException("error.role_not_found"));

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

    private boolean isProtectedAdmin(User user) {
        return user != null && PROTECTED_ADMIN_EMAIL.equalsIgnoreCase(user.getEmail());
    }

    private String msg(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, key, locale);
    }
}