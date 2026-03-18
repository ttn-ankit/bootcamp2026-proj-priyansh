package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerceproject.dto.AddressPartialUpdateRequestDTO;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CategoryMetadataDTO;
import com.example.ecommerceproject.dto.PasswordUpdateRequestDTO;
import com.example.ecommerceproject.dto.SellerCategoryResponseDTO;
import com.example.ecommerceproject.dto.SellerProfileResponseDTO;
import com.example.ecommerceproject.dto.SellerProfileUpdateRequestDTO;
import com.example.ecommerceproject.entity.Address;
import com.example.ecommerceproject.entity.Category;
import com.example.ecommerceproject.entity.Seller;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.repository.AddressRepository;
import com.example.ecommerceproject.repository.CategoryRepository;
import com.example.ecommerceproject.repository.SellerRepository;
import com.example.ecommerceproject.service.EmailService;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.service.SellerService;
import com.example.ecommerceproject.service.UserSessionService;
import com.example.ecommerceproject.util.MessageKeys;
import com.example.ecommerceproject.enums.AddressType;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class SellerServiceImpl implements SellerService {

    @Value("${app.image.base-path}")
    String basePath;

    final SellerRepository sellerRepository;
    final AddressRepository addressRepository;
    final CategoryRepository categoryRepository;
    final PasswordEncoder passwordEncoder;
    final EmailService emailService;
    final MessageService messageService;
    final UserSessionService userSessionService;
    final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public SellerProfileResponseDTO getProfile(Long userId) {
        validateUserAccess(userId);
        Seller seller = getActiveSellerByUserId(userId);
        User user = seller.getUser();
        List<Address> addresses = addressRepository.findByUserAndUserIsDeletedFalse(user);
        Address address = addresses.isEmpty() ? new Address() : addresses.get(0);

        SellerProfileResponseDTO response = modelMapper.map(seller, SellerProfileResponseDTO.class);
        modelMapper.map(user, response);
        modelMapper.map(address, response);
        response.setId(user.getId());
        response.setImage(computeImageUrl(user.getId(), seller));
        response.setAddressId(address.getId());

        return response;
    }

    @Override
    @Transactional
    public ApiResponseDTO updateProfile(Long userId, SellerProfileUpdateRequestDTO dto) {
        validateUserAccess(userId);
        Seller seller = getActiveSellerByUserId(userId);

        modelMapper.map(dto, seller);
        modelMapper.map(dto, seller.getUser());

        return new ApiResponseDTO(messageService.get(MessageKeys.SELLER_PROFILE_UPDATED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO updatePassword(Long userId, PasswordUpdateRequestDTO dto) {
        validateUserAccess(userId);
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ApiException(messageService.get(MessageKeys.VALIDATION_PASSWORD_DO_NOT_MATCH), 400);
        }

        Seller seller = getActiveSellerByUserId(userId);
        User user = seller.getUser();

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setPasswordUpdateDate(LocalDateTime.now());

        userSessionService.revokeAllRefreshTokens(user);

        emailService.sendPasswordChangedEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.SELLER_PASSWORD_UPDATED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO updateAddress(Long userId, AddressPartialUpdateRequestDTO dto) {
        validateUserAccess(userId);
        Seller seller = getActiveSellerByUserId(userId);
        Address address = addressRepository.findByUserAndUserIsDeletedFalse(seller.getUser()).stream().findFirst()
                .orElseThrow(
                        () -> new ApiException("Address not found", 400));

        if (dto.getLabel() != null) {
            validateSellerAddressLabel(dto.getLabel());
        }

        modelMapper.map(dto, address);

        return new ApiResponseDTO(messageService.get(MessageKeys.SELLER_ADDRESS_UPDATED), 200);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SellerCategoryResponseDTO> getAllLeafCategories(){
        List<Category> leafNodes = categoryRepository.findAllLeafNodes();
        return leafNodes.stream().map(this::mapToSellerCategoryDTO).collect(Collectors.toList());
    }


    private SellerCategoryResponseDTO mapToSellerCategoryDTO(Category category) {
        SellerCategoryResponseDTO dto = new SellerCategoryResponseDTO();
        dto.setCategoryId(category.getId());
        dto.setCategoryName(category.getName());
        dto.setParentChain(buildParentChain(category));
        
        List<CategoryMetadataDTO> metadataDTOs = category.getFieldValues().stream().map(fv -> {
            CategoryMetadataDTO metaDto = new CategoryMetadataDTO();
            metaDto.setMetadataFieldId(fv.getMetadataField().getId());
            metaDto.setFieldName(fv.getMetadataField().getName());
            metaDto.setPossibleValues(fv.getValue());
            return metaDto;
        }).collect(Collectors.toList());
        
        dto.setMetadataFields(metadataDTOs);

        return dto;
    }

    private String buildParentChain(Category category) {
        StringBuilder chain = new StringBuilder(category.getName());
        Category parent = category.getParentCategory();
        
        while (parent != null) {
            chain.insert(0, parent.getName() + " > ");
            parent = parent.getParentCategory();
        }
        
        return chain.toString();
    }

    private Seller getActiveSellerByUserId(Long userId) {
        Seller seller = sellerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.ERROR_SELLER_NOT_FOUND), 400));

        if (!seller.getUser().isActive()) {
            throw new ApiException(messageService.get(MessageKeys.AUTH_ACCOUNT_NOT_ACTIVATED), 400);
        }

        return seller;
    }

    private String computeImageUrl(Long userId, Seller seller) {
        File userDir = Paths.get(basePath, "users").toFile();
        if (!userDir.exists() || !userDir.isDirectory()) {
            return null;
        }
        Long[] idsToTry = {userId, seller != null ? seller.getId() : null};
        
        for (Long id : idsToTry) {
            if (id == null) continue;
            
            String imageUrl = findImageForId(userDir, id);
            if (imageUrl != null) {
                return imageUrl;
            }
        }
        
        return null;
    }

    private String findImageForId(File directory, Long id) {
        File[] files = directory.listFiles((dir, name) -> {
            String lowerName = name.toLowerCase();
            return lowerName.startsWith(id + ".") && 
                   (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || 
                    lowerName.endsWith(".png") || lowerName.endsWith(".gif"));
        });
        
        return (files != null && files.length > 0) ? "/images/users/" + files[0].getName() : null;
    }

    private void validateSellerAddressLabel(AddressType label) {
        if (label == AddressType.HOME) {
            throw new ApiException(messageService.get(MessageKeys.VALIDATION_INVALID_SELLER_ADDRESS_LABEL), 400);
        }
    }

    private void validateUserAccess(Long requestedUserId) {
        Long authenticatedUserId = getCurrentUserId();
        if (!requestedUserId.equals(authenticatedUserId)) {
            throw new ApiException(messageService.get(MessageKeys.ERROR_ACCESS_DENIED), 403);
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        throw new ApiException(messageService.get(MessageKeys.AUTH_USER_NOT_AUTHENTICATED), 401);
    }
}