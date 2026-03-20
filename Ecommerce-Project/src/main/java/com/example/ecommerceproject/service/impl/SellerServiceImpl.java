package com.example.ecommerceproject.service.impl;

import com.example.ecommerceproject.repository.ProductRepository;
import com.example.ecommerceproject.repository.ProductVariationRepository;

import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerceproject.dto.AddressPartialUpdateRequestDTO;
import com.example.ecommerceproject.dto.ApiResponse;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CategoryMetadataDTO;
import com.example.ecommerceproject.dto.PasswordUpdateRequestDTO;
import com.example.ecommerceproject.dto.ProductCreateRequest;
import com.example.ecommerceproject.dto.ProductResponse;
import com.example.ecommerceproject.dto.ProductUpdateRequest;
import com.example.ecommerceproject.dto.ProductVariationCreateRequest;
import com.example.ecommerceproject.dto.ProductVariationResponse;
import com.example.ecommerceproject.dto.ProductVariationUpdateRequest;
import com.example.ecommerceproject.dto.SellerCategoryResponseDTO;
import com.example.ecommerceproject.dto.SellerProfileResponseDTO;
import com.example.ecommerceproject.dto.SellerProfileUpdateRequestDTO;
import com.example.ecommerceproject.entity.Address;
import com.example.ecommerceproject.entity.Category;
import com.example.ecommerceproject.entity.CategoryMetadataFieldValues;
import com.example.ecommerceproject.entity.Product;
import com.example.ecommerceproject.entity.ProductVariations;
import com.example.ecommerceproject.entity.Seller;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.repository.AddressRepository;
import com.example.ecommerceproject.repository.CategoryMetadataFieldValuesRepository;
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
    final ProductRepository productRepository;
    final AddressRepository addressRepository;
    final CategoryRepository categoryRepository;
    final CategoryMetadataFieldValuesRepository metadataFieldRepository;
    final ProductVariationRepository variationRepository;
    final PasswordEncoder passwordEncoder;
    final EmailService emailService;
    final MessageService messageService;
    final UserSessionService userSessionService;
    final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public SellerProfileResponseDTO getProfile(Long userId) {
        validateUserAccess(userId);
        validateSellerRole();
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
                        () -> new ApiException(messageService.get(MessageKeys.ERROR_ADDRESS_NOT_FOUND), 400));

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

    @Override
    @Transactional
    public ApiResponse createProduct(Long sellerId, ProductCreateRequest dto) {
        if(!categoryRepository.isLeafNode(dto.getCategoryId())){
            throw new ApiException(messageService.get(MessageKeys.CATEGORY_MUST_BE_VALID_LEAF), 400);
        }
        if(productRepository.existsByNameAndBrandAndCategory_IdAndSeller_Id(dto.getName(), dto.getBrand(), dto.getCategoryId(), sellerId)){
            throw new ApiException(messageService.get(MessageKeys.PRODUCT_MUST_BE_UNIQUE_FOR_BRAND_AND_CATEGORY), 400);
        }
        Product product = modelMapper.map(dto, Product.class);
        
        Seller seller = sellerRepository.getReferenceById(sellerId);
        Category category = categoryRepository.getReferenceById(dto.getCategoryId());

        product.setSeller(seller);
        product.setCategory(category);
        product.setIsActive(false);
        productRepository.save(product);

        return new ApiResponse(messageService.get(MessageKeys.PRODUCT_ADDED_SUCCESSFULLY));
    }

    @Override
    public ApiResponse createProductVariation(Long sellerId, Long productId, ProductVariationCreateRequest dto) {
        Product product = productRepository.findById(productId).orElseThrow(
            () -> new ApiException(messageService.get(MessageKeys.PRODUCT_NOT_FOUND), 400)
        );

        if(!product.getIsActive()){
            throw new ApiException(messageService.get(MessageKeys.PRODUCT_MUST_BE_ACTIVE_TO_ADD_VARIATION), 400);
        }
        validateImageNaming(productId, dto.getPrimaryImageUrl(), dto.getSecondaryImageUrls());
        validateVariationMetadata(product.getCategory().getId(), productId, dto.getMetadata());

        ProductVariations variations = modelMapper.map(dto, ProductVariations.class);
        variations.setProduct(product);
        variations.setIsActive(true);
        variationRepository.save(variations);

        return new ApiResponse(messageService.get(MessageKeys.PRODUCT_VARIATION_CREATED_SUCCESSFULLY));
    }   

    @Override
    public Page<ProductResponse> getAllProducts(Long sellerId, int offset, int max, String sort, String order) {
        Pageable pageable = PageRequest.of(offset, max, Sort.Direction.fromString(order.toUpperCase()), sort);
        return productRepository.findAllBySeller_IdAndIsDeletedFalse(sellerId, pageable)
                .map(product -> modelMapper.map(product, ProductResponse.class));
    }

    @Override
    public Page<ProductVariationResponse> getProductVariations(Long sellerId, Long productId, int offset, int max,
            String sort, String order) {
        productRepository.findByIdAndSellerIdAndIsDeletedFalse(productId, sellerId)
                .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.INVALID_PRODUCT_ID), 400));

        Pageable pageable = PageRequest.of(offset, max, Sort.Direction.fromString(order.toUpperCase()), sort);
        return variationRepository.findAllByProductId(productId, pageable)
                .map(variation -> modelMapper.map(variation, ProductVariationResponse.class));
    }

    @Override
    @Transactional
    public ApiResponse deleteProduct(Long sellerId, Long productId) {
        Product product = productRepository.findByIdAndSellerIdAndIsDeletedFalse(sellerId, productId).orElseThrow(
            () -> new ApiException(messageService.get(MessageKeys.INVALID_PRODUCT_ID), 400)
        );
        productRepository.delete(product);
        return new ApiResponse(messageService.get(MessageKeys.PRODUCT_DELETED_SUCCESSFULLY));
    }

    @Override
    public ApiResponse updateProduct(Long sellerId, Long productId, ProductUpdateRequest dto) {
        Product existingProduct = productRepository.findByIdAndSellerIdAndIsDeletedFalse(productId, sellerId)
                .orElseThrow(() -> new RuntimeException("Validation Error: Invalid Product ID or unauthorized."));

        if (dto.getName() != null && !dto.getName().equals(existingProduct.getName())) {
             if (productRepository.existsByNameAndBrandAndCategory_IdAndSeller_Id(
                    dto.getName(), existingProduct.getBrand(), existingProduct.getCategory().getId(), sellerId)) {
                throw new ApiException(messageService.get(MessageKeys.PRODUCT_NAME_EXISTS), 400);
            }
        }

        modelMapper.map(dto, existingProduct);
        return new ApiResponse(messageService.get(MessageKeys.PRODUCT_UPDATED_SUCCESSFULLY));
    }

    @Override
    public ApiResponse updateProductVariation(Long sellerId, Long productId, Long variationId,
            ProductVariationUpdateRequest dto) {
        Product product = productRepository.findByIdAndSellerIdAndIsDeletedFalse(productId, sellerId)
                .orElseThrow(() -> new RuntimeException("Validation Error: Invalid Product ID or unauthorized."));
        
        if (!product.getIsActive()) {
            throw new ApiException(messageService.get(MessageKeys.PRODUCT_MUST_BE_ACTIVE), 400);
        }

        ProductVariations existingVariation = variationRepository.findByIdAndProductId(variationId, productId)
                .orElseThrow(() -> new RuntimeException("Validation Error: Invalid Product Variation ID."));

        validateImageNaming(productId, dto.getPrimaryImageUrl(), dto.getSecondaryImageUrls());
        if (dto.getMetadata() != null) {
            validateVariationMetadata(product.getCategory().getId(), productId, dto.getMetadata());
        }

        modelMapper.map(dto, existingVariation);
        return new ApiResponse(messageService.get(MessageKeys.PRODUCT_VARIATION_UPDATED_SUCCESSFULLY));
    }

    private void validateImageNaming(Long productId, String primaryImage, List<String> secondaryImages) {
        if (primaryImage != null) {
            String expectedPrimaryPattern = "(?i).*\\b" + productId + "\\.(jpg|png)$";
            if (!primaryImage.matches(expectedPrimaryPattern)) {
                throw new ApiException(messageService.get(MessageKeys.PRODUCT_PRIMARY_IMAGE_FORMAT, new Object[]{productId}), 400);
            }
        }

        if (secondaryImages != null && !secondaryImages.isEmpty()) {
            String expectedSecondaryPattern = "(?i).*\\bimage_\\d+\\.(jpg|png)$";
            for (String secImage : secondaryImages) {
                if (!secImage.matches(expectedSecondaryPattern)) {
                    throw new ApiException(messageService.get(MessageKeys.PRODUCT_SECONDARY_IMAGE_FORMAT), 400);
                }
            }
        }
    }

    private void validateVariationMetadata(Long categoryId, Long productId, Map<String, String> incomingMetadata) {
        if (incomingMetadata == null || incomingMetadata.isEmpty()) {
            throw new ApiException(messageService.get(MessageKeys.VARIATION_MUST_HAVE_ONE_VALUE), 400);
        }

        List<CategoryMetadataFieldValues> allowedFieldsFromDb = metadataFieldRepository.findAllByCategory_Id(categoryId);
        if (allowedFieldsFromDb.isEmpty()) {
            throw new ApiException(messageService.get(MessageKeys.PRODUCT_NO_METADATA_FIELDS), 400);
        }

        Map<String, List<String>> allowedMetadataMap = new HashMap<>();
        for (CategoryMetadataFieldValues cmfv : allowedFieldsFromDb) {
            String key = cmfv.getMetadataField().getName().toLowerCase();
            List<String> values = Arrays.stream(cmfv.getValue().split(","))
                                        .map(String::trim)
                                        .map(String::toLowerCase)
                                        .collect(Collectors.toList());
            allowedMetadataMap.put(key, values);
        }

        for (Map.Entry<String, String> entry : incomingMetadata.entrySet()) {
            String inputKey = entry.getKey().trim().toLowerCase();
            String inputValue = entry.getValue().trim().toLowerCase();

            if (!allowedMetadataMap.containsKey(inputKey)) {
                throw new ApiException(messageService.get(MessageKeys.PRODUCT_INVALID_METADATA_FIELD, new Object[]{inputKey}), 400);
            }

            List<String> allowedValues = allowedMetadataMap.get(inputKey);
            if (!allowedValues.contains(inputValue)) {
                throw new ApiException(messageService.get(MessageKeys.PRODUCT_INVALID_METADATA_VALUE, new Object[]{entry.getValue(), allowedValues}), 400);
            }
        }

        Page<ProductVariations> existingVariations = variationRepository.findAllByProductId(productId, PageRequest.of(0, 1));
        if (existingVariations.hasContent()) {
            Set<String> existingKeys = existingVariations.getContent().get(0).getMetadata().keySet();
            Set<String> newKeys = incomingMetadata.keySet();

            if (!existingKeys.equals(newKeys)) {
                throw new ApiException(messageService.get(MessageKeys.PRODUCT_VARIATION_KEYS_MISMATCH, new Object[]{existingKeys}), 400);
            }
        }
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
        try {
            Seller seller = sellerRepository.findByUser_Id(userId)
                    .orElseThrow(() -> new ApiException(messageService.get(MessageKeys.ERROR_SELLER_NOT_FOUND), 404));

            if (!seller.getUser().isActive()) {
                throw new ApiException(messageService.get(MessageKeys.AUTH_ACCOUNT_NOT_ACTIVATED), 400);
            }

            return seller;
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(messageService.get(MessageKeys.ERROR_SELLER_NOT_FOUND), 404);
        }
    }

    private String computeImageUrl(Long userId, Seller seller) {
        File userDir = Paths.get(basePath, messageService.get(MessageKeys.DIRECTORY_USERS)).toFile();
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
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
            throw new ApiException(messageService.get(MessageKeys.AUTH_USER_NOT_AUTHENTICATED), 401);
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(messageService.get(MessageKeys.AUTH_USER_NOT_AUTHENTICATED), 401);
        }
    }

    private void validateSellerRole() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(messageService.get(MessageKeys.ROLE_SELLER)))) {
                throw new ApiException(messageService.get(MessageKeys.ERROR_ACCESS_DENIED), 403);
            }
        } catch (Exception e) {
            if (e instanceof ApiException) {
                throw e;
            }
            throw new ApiException(messageService.get(MessageKeys.ERROR_ACCESS_DENIED), 403);
        }
    }
}