package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;

import org.springframework.data.domain.Pageable;
import com.example.ecommerceproject.exception.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CustomerResponseDTO;
import com.example.ecommerceproject.dto.SellerResponseDTO;
import com.example.ecommerceproject.entity.Address;
import com.example.ecommerceproject.entity.Customer;
import com.example.ecommerceproject.entity.Seller;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.exception.ResourceNotFoundException;
import com.example.ecommerceproject.repository.AddressRepository;
import com.example.ecommerceproject.repository.CustomerRepository;
import com.example.ecommerceproject.repository.SellerRepository;
import com.example.ecommerceproject.repository.UserRepository;
import com.example.ecommerceproject.service.AdminService;
import com.example.ecommerceproject.service.EmailService;
import com.example.ecommerceproject.service.UserSessionService;
import com.example.ecommerceproject.util.MessageKeys;
import com.example.ecommerceproject.service.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AdminServiceImpl implements AdminService{
    
    static final String PROTECTED_ADMIN_EMAIL = "admin@ecommerce.com";

    final CustomerRepository customerRepository;
    final SellerRepository sellerRepository;
    final UserRepository userRepository;
    final AddressRepository addressRepository;
    final EmailService emailService;
    final MessageService messageService;
    final ModelMapper modelMapper;
    final UserSessionService userSessionService;

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(int page, int size, String sort, String email) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        Page<Customer> customers;
        if(email != null && !email.isBlank()){
            customers = customerRepository.findByUser_EmailContainingIgnoreCase(email, pageable);
        } else {
            customers = customerRepository.findAll(pageable);
        }

        return customers.map(this::mapToCustomerDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SellerResponseDTO> getAllSellers(int page, int size, String sort, String email) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Seller> sellers;

        if(email != null && !email.isBlank()){
            sellers = sellerRepository.findByUser_EmailContainingIgnoreCase(email, pageable);
        } else {
            sellers = sellerRepository.findAll(pageable);
        }
        return sellers.map(this::mapToSellerDTO);
    }

    @Override
    @Transactional
    public ApiResponseDTO activateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException(MessageKeys.ERROR_CUSTOMER_NOT_FOUND));

        User user = customer.getUser();
        validateUserNotDeleted(user);

        if(user.isActive()){
            throw new BadRequestException(MessageKeys.VALIDATION_USER_ALREADY_DELETED);
        }

        user.setActive(true);
        userRepository.save(user);
        emailService.sendAccountActivationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.ADMIN_CUSTOMER_ACTIVATED));
    }

    @Override
    @Transactional
    public ApiResponseDTO deactivateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException(MessageKeys.ERROR_CUSTOMER_NOT_FOUND));
        User user = customer.getUser();

        validateUserNotDeleted(user);
        validateNotProtectedAdmin(user);

        if(!user.isActive()){
            throw new BadRequestException(MessageKeys.VALIDATION_USER_ALREADY_DEACTIVATED);
        }

        user.setActive(false);
        userRepository.save(user);
        
        userSessionService.revokeAllRefreshTokens(user);
        
        emailService.sendAccountDeactivationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.ADMIN_CUSTOMER_DEACTIVATED));
    }

    @Override
    @Transactional
    public ApiResponseDTO activateSeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException(MessageKeys.ERROR_SELLER_NOT_FOUND));
        User user = seller.getUser();

        validateUserNotDeleted(user);

        if(user.isActive()){
            throw new BadRequestException(MessageKeys.VALIDATION_USER_ALREADY_DELETED);
        }

        user.setActive(true);
        userRepository.save(user);
        emailService.sendAccountActivationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.ADMIN_SELLER_ACTIVATED));
    }

    @Override
    @Transactional
    public ApiResponseDTO deactivateSeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException(MessageKeys.ERROR_SELLER_NOT_FOUND));
        User user = seller.getUser();

        validateUserNotDeleted(user);
        validateNotProtectedAdmin(user);

        if(!user.isActive()){
            throw new BadRequestException(MessageKeys.VALIDATION_USER_ALREADY_DEACTIVATED);
        }

        user.setActive(false);
        userRepository.save(user);
        
        // Revoke all active sessions for this user
        userSessionService.revokeAllRefreshTokens(user);
        
        emailService.sendAccountDeactivationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.ADMIN_SELLER_DEACTIVATED));
    }

    private void validateNotProtectedAdmin(User user) {
        if (user != null && PROTECTED_ADMIN_EMAIL.equalsIgnoreCase(user.getEmail())) {
            throw new BadRequestException(MessageKeys.AUTH_ADMIN_PROTECTED);
        }
    }

    private void validateUserNotDeleted(User user) {
        if (user.isDeleted()) {
            throw new BadRequestException(MessageKeys.ERROR_USER_IS_DELETED);
        }
    }

    private CustomerResponseDTO mapToCustomerDTO(Customer customer) {
        CustomerResponseDTO dto = modelMapper.map(customer, CustomerResponseDTO.class);
        
        User user = customer.getUser();
        dto.setFullName(buildFullName(user));
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        
        return dto;
    }

    private SellerResponseDTO mapToSellerDTO(Seller seller) {
        SellerResponseDTO dto = modelMapper.map(seller, SellerResponseDTO.class);
        
        User user = seller.getUser();
        dto.setFullName(buildFullName(user));
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        dto.setCompanyAddress(fetchAndFormatAddress(user));
        
        return dto;
    }

    private String fetchAndFormatAddress(User user) {
        List<Address> addresses = addressRepository.findByUser(user);
        if (addresses == null || addresses.isEmpty()) {
            return "N/A";
        }
        
        Address addr = addresses.get(0); 
        return String.format("%s, %s, %s - %s, %s",
                addr.getAddressLine() != null ? addr.getAddressLine() : "",
                addr.getCity() != null ? addr.getCity() : "",
                addr.getState() != null ? addr.getState() : "",
                addr.getZipCode() != null ? addr.getZipCode() : "",
                addr.getCountry() != null ? addr.getCountry() : ""
        ).replaceAll(", ,", ",").replaceAll(" - ,", ",");
    }

    private String buildFullName(User user) {
        return (user.getFirstName() + " " + 
               (user.getMiddleName() != null ? user.getMiddleName() + " " : "") + 
               user.getLastName()).trim().replaceAll(" +", " ");
    }
}
