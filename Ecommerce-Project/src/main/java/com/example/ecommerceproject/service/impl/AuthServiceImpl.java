package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.LoginRequestDTO;
import com.example.ecommerceproject.dto.LoginResponseDTO;
import com.example.ecommerceproject.dto.RegisterRequestDTO;
import com.example.ecommerceproject.dto.SellerRegisterRequestDTO;
import com.example.ecommerceproject.entity.*;
import com.example.ecommerceproject.enums.AddressLabelEnums;
import com.example.ecommerceproject.enums.RoleEnums;
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

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

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
    public LoginResponseDTO login(LoginRequestDTO dto){
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        User user = userRepository.findByEmailAndIsDeletedFalse(dto.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(user);
        return new LoginResponseDTO(token, "Login Successfull");
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