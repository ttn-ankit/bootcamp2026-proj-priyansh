package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Pageable;
import com.example.ecommerceproject.exception.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    final MessageSource messageSource;
    final ModelMapper modelMapper;

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
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("error.customer_not_found"));

        User user = customer.getUser();
        validateUserNotDeleted(user);

        if(user.isActive()){
            throw new BadRequestException("validation.user_already_deleted");
        }

        user.setActive(true);
        userRepository.save(user);
        emailService.sendAccountActivationEmail(user.getEmail());

        return new ApiResponseDTO(msg("admin.customer_activated"));
    }

    @Override
    @Transactional
    public ApiResponseDTO deactivateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("error.customer_not_found"));
        User user = customer.getUser();

        validateUserNotDeleted(user);
        validateNotProtectedAdmin(user);

        if(!user.isActive()){
            throw new BadRequestException("validation.user_already_deactivated");
        }

        user.setActive(false);
        userRepository.save(user);
        emailService.sendAccountDeactivationEmail(user.getEmail());

        return new ApiResponseDTO(msg("admin.customer_deactivated"));
    }

    @Override
    @Transactional
    public ApiResponseDTO activateSeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException("error.seller_not_found"));
        User user = seller.getUser();

        validateUserNotDeleted(user);

        if(user.isActive()){
            throw new BadRequestException("validation.user_already_deleted");
        }

        user.setActive(true);
        userRepository.save(user);
        emailService.sendAccountActivationEmail(user.getEmail());

        return new ApiResponseDTO(msg("admin.seller_activated"));
    }

    @Override
    @Transactional
    public ApiResponseDTO deactivateSeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException("error.seller_not_found"));
        User user = seller.getUser();

        validateUserNotDeleted(user);
        validateNotProtectedAdmin(user);

        if(!user.isActive()){
            throw new BadRequestException("validation.user_already_deactivated");
        }

        user.setActive(false);
        userRepository.save(user);
        emailService.sendAccountDeactivationEmail(user.getEmail());

        return new ApiResponseDTO(msg("admin.seller_deactivated"));
    }

    private void validateNotProtectedAdmin(User user) {
        if (user != null && PROTECTED_ADMIN_EMAIL.equalsIgnoreCase(user.getEmail())) {
            throw new BadRequestException("auth.admin_protected");
        }
    }

    private void validateUserNotDeleted(User user) {
        if (user.isDeleted()) {
            throw new BadRequestException("error.user_is_deleted");
        }
    }

    private String msg(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (Exception e) {
            return key; 
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
