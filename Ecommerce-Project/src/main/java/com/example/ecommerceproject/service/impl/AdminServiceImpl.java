package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import com.example.ecommerceproject.exception.ApiException;
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
import com.example.ecommerceproject.repository.AddressRepository;
import com.example.ecommerceproject.repository.CustomerRepository;
import com.example.ecommerceproject.repository.SellerRepository;
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
public class AdminServiceImpl implements AdminService {

    final CustomerRepository customerRepository;
    final SellerRepository sellerRepository;
    final AddressRepository addressRepository;
    final EmailService emailService;
    final MessageService messageService;
    final ModelMapper modelMapper;
    final UserSessionService userSessionService;

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(int page, int size, String sort, String email) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sort).ascending());

        Page<Customer> customers;
        if (email != null && !email.isBlank()) {
            customers = customerRepository.findByUser_EmailContainingIgnoreCase(email, pageable);
        } else {
            customers = customerRepository.findAll(pageable);
        }

        return customers.map(this::mapToCustomerDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SellerResponseDTO> getAllSellers(int page, int size, String sort, String email) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sort).ascending());
        Page<Seller> sellers;

        if (email != null && !email.isBlank()) {
            sellers = sellerRepository.findByUser_EmailContainingIgnoreCase(email, pageable);
        } else {
            sellers = sellerRepository.findAll(pageable);
        }
        return sellers.map(this::mapToSellerDTO);
    }

    private Sort buildSort(String sort) {
        return switch (sort.toLowerCase()) {
            case "email" -> Sort.by("user.email");
            case "firstname" -> Sort.by("user.firstName");
            case "lastname" -> Sort.by("user.lastName");
            case "name", "fullname" -> Sort.by("user.firstName");
            case "active", "isactive" -> Sort.by("user.isActive");
            case "created", "createdate" -> Sort.by("user.createdDate");
            case "updated", "updatedate" -> Sort.by("user.updatedDate");
            case "id" -> Sort.by("id");
            default -> Sort.by("id"); 
        };
    }

    @Override
    @Transactional
    public ApiResponseDTO activateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException(MessageKeys.ERROR_CUSTOMER_NOT_FOUND, 404));

        User user = customer.getUser();
        validateUserNotDeleted(user);

        if (user.isActive()) {
            throw new ApiException(MessageKeys.VALIDATION_USER_ALREADY_ACTIVATED, 400);
        }

        user.setActive(true);
        emailService.sendAccountActivationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.ADMIN_CUSTOMER_ACTIVATED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO deactivateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException(MessageKeys.ERROR_CUSTOMER_NOT_FOUND, 404));
        User user = customer.getUser();

        validateUserNotDeleted(user);
        validateNotProtectedAdmin(user);

        if (!user.isActive()) {
            throw new ApiException(MessageKeys.VALIDATION_USER_ALREADY_DEACTIVATED, 400);
        }

        user.setActive(false);

        userSessionService.revokeAllRefreshTokens(user);

        emailService.sendAccountDeactivationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.ADMIN_CUSTOMER_DEACTIVATED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO activateSeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ApiException(MessageKeys.ERROR_SELLER_NOT_FOUND, 404));
        User user = seller.getUser();

        validateUserNotDeleted(user);

        if (user.isActive()) {
            throw new ApiException(MessageKeys.VALIDATION_USER_ALREADY_ACTIVATED, 400);
        }

        user.setActive(true);
        emailService.sendAccountActivationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.ADMIN_SELLER_ACTIVATED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO deactivateSeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ApiException(MessageKeys.ERROR_SELLER_NOT_FOUND, 404));
        User user = seller.getUser();

        validateUserNotDeleted(user);
        validateNotProtectedAdmin(user);

        if (!user.isActive()) {
            throw new ApiException(MessageKeys.VALIDATION_USER_ALREADY_DEACTIVATED, 400);
        }

        user.setActive(false);

        userSessionService.revokeAllRefreshTokens(user);

        emailService.sendAccountDeactivationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.ADMIN_SELLER_DEACTIVATED), 200);
    }

    private void validateNotProtectedAdmin(User user) {
        if (user != null && MessageKeys.PROTECTED_ADMIN_EMAIL.equalsIgnoreCase(user.getEmail())) {
            throw new ApiException(MessageKeys.AUTH_ADMIN_PROTECTED, 400);
        }
    }

    private void validateUserNotDeleted(User user) {
        if (user.isDeleted()) {
            throw new ApiException(MessageKeys.ERROR_USER_IS_DELETED, 400);

        }
    }

    private CustomerResponseDTO mapToCustomerDTO(Customer customer) {
        CustomerResponseDTO dto = modelMapper.map(customer, CustomerResponseDTO.class);

        User user = customer.getUser();
        dto.setId(user.getId());
        dto.setFullName(buildFullName(user));
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());

        return dto;
    }

    private SellerResponseDTO mapToSellerDTO(Seller seller) {
        SellerResponseDTO dto = modelMapper.map(seller, SellerResponseDTO.class);

        User user = seller.getUser();
        dto.setId(user.getId());
        dto.setFullName(buildFullName(user));
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        dto.setCompanyAddress(fetchAndFormatAddress(user));

        return dto;
    }

    private String fetchAndFormatAddress(User user) {
        List<Address> addresses = addressRepository.findByUserAndUserIsDeletedFalse(user); 
        
        if (addresses == null || addresses.isEmpty()) {
            return "N/A";
        }

        Address addr = addresses.get(0); 

        String formattedAddress = Stream.of(
                    addr.getAddressLine(), 
                    addr.getCity(), 
                    addr.getState(), 
                    addr.getZipCode(), 
                    addr.getCountry()
                )
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(", "));

        return formattedAddress.isEmpty() ? "N/A" : formattedAddress;
    }

    private String buildFullName(User user) {
        return (user.getFirstName() + " " +
                (user.getMiddleName() != null ? user.getMiddleName() + " " : "") +
                user.getLastName()).trim().replaceAll(" +", " ");
    }
}
