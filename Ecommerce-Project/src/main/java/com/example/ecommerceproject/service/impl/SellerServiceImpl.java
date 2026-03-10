package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerceproject.dto.AddressUpdateRequestDTO;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.SellerPasswordUpdateRequestDTO;
import com.example.ecommerceproject.dto.SellerProfileResponseDTO;
import com.example.ecommerceproject.dto.SellerProfileUpdateRequestDTO;
import com.example.ecommerceproject.entity.Address;
import com.example.ecommerceproject.entity.Seller;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.exception.BadRequestException;
import com.example.ecommerceproject.exception.ResourceNotFoundException;
import com.example.ecommerceproject.repository.AddressRepository;
import com.example.ecommerceproject.repository.SellerRepository;
import com.example.ecommerceproject.repository.UserRepository;
import com.example.ecommerceproject.service.EmailService;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.service.SellerService;
import com.example.ecommerceproject.util.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class SellerServiceImpl implements SellerService {

    @Value("${app.image.base-path:uploads}")
    String basePath;

    final SellerRepository sellerRepository;
    final UserRepository userRepository;
    final AddressRepository addressRepository;
    final PasswordEncoder passwordEncoder;
    final EmailService emailService;
    final MessageService messageService;

    @Override
    @Transactional(readOnly = true)
    public SellerProfileResponseDTO getProfile(Long userId) {

        Seller seller = getActiveSellerByUserId(userId);
        User user = seller.getUser();

        List<Address> addresses = addressRepository.findByUser(user);
        Address address = addresses.isEmpty() ? new Address() : addresses.get(0);

        String imageUrl = computeImageUrl(user.getId());

        return SellerProfileResponseDTO.builder()
                .id(seller.getId())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .isActive(user.isActive())
                .companyContact(seller.getCompanyContact())
                .companyName(seller.getCompanyName())
                .image(imageUrl)
                .gst(seller.getGst())
                .addressId(address.getId())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .build();
    }

    @Override
    @Transactional
    public ApiResponseDTO updateProfile(Long userId, SellerProfileUpdateRequestDTO dto) {
        Seller seller = getActiveSellerByUserId(userId);
        User user = seller.getUser();

        user.setFirstName(dto.getFirstName());
        user.setMiddleName(dto.getMiddleName());
        user.setLastName(dto.getLastName());

        seller.setCompanyName(dto.getCompanyName());
        seller.setCompanyContact(dto.getCompanyContact());

        userRepository.save(user);
        sellerRepository.save(seller);

        return new ApiResponseDTO(messageService.get(MessageKeys.SELLER_PROFILE_UPDATED));
    }

    @Override
    @Transactional
    public ApiResponseDTO updatePassword(Long userId, SellerPasswordUpdateRequestDTO dto) {
        if(!dto.getPassword().equals(dto.getConfirmPassword())){
            throw new BadRequestException(messageService.get(MessageKeys.VALIDATION_PASSWORD_DO_NOT_MATCH));
        }

        Seller seller = getActiveSellerByUserId(userId);
        User user = seller.getUser();

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        emailService.sendPasswordChangedEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.SELLER_PASSWORD_UPDATED));
    }

    @Override
    @Transactional
    public ApiResponseDTO updateAddress(Long userId, Long addressId, AddressUpdateRequestDTO dto) {
        getActiveSellerByUserId(userId);

        Address address = addressRepository.findById(addressId)
        .orElseThrow(() -> new ResourceNotFoundException(messageService.get(MessageKeys.ERROR_ADDRESS_NOT_FOUND)));

        address.setAddressLine(dto.getAddressLine());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());

        addressRepository.save(address);

        return new ApiResponseDTO(messageService.get(MessageKeys.SELLER_ADDRESS_UPDATED));
    }

    private Seller getActiveSellerByUserId(Long userId) {
        Seller seller = sellerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException(messageService.get(MessageKeys.ERROR_SELLER_NOT_FOUND)));

        if (!seller.getUser().isActive()) {
            throw new BadRequestException(messageService.get(MessageKeys.AUTH_ACCOUNT_NOT_ACTIVATED));
        }

        return seller;
    }

    private String computeImageUrl(Long userId) {
        File userDir = Paths.get(basePath, "users").toFile();
        if (userDir.exists() && userDir.isDirectory()) {
            File[] files = userDir.listFiles((dir, name) -> name.startsWith(userId + "."));
            if(files != null && files.length > 0){
                return "/images/users/" + files[0].getName();
            }
        }
        return null;
    }

}
