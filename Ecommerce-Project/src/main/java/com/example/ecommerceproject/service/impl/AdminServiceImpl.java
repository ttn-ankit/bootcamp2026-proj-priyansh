package com.example.ecommerceproject.service.impl;

import static lombok.AccessLevel.PRIVATE;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import com.example.ecommerceproject.exception.ApiException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerceproject.dto.ApiResponse;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CategoryMetadataValueRequestDTO;
import com.example.ecommerceproject.dto.CategoryResponseDTO;
import com.example.ecommerceproject.dto.CustomerResponseDTO;
import com.example.ecommerceproject.dto.MetadataFieldResponseDTO;
import com.example.ecommerceproject.dto.ProductResponseDTO;
import com.example.ecommerceproject.dto.SellerResponseDTO;
import com.example.ecommerceproject.entity.Address;
import com.example.ecommerceproject.entity.Category;
import com.example.ecommerceproject.entity.CategoryMetadataField;
import com.example.ecommerceproject.entity.CategoryMetadataFieldValues;
import com.example.ecommerceproject.entity.Customer;
import com.example.ecommerceproject.entity.Product;
import com.example.ecommerceproject.entity.Seller;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.repository.AddressRepository;
import com.example.ecommerceproject.repository.CategoryMetadataFieldRepository;
import com.example.ecommerceproject.repository.CategoryMetadataFieldValuesRepository;
import com.example.ecommerceproject.repository.CategoryRepository;
import com.example.ecommerceproject.repository.CustomerRepository;
import com.example.ecommerceproject.repository.ProductRepository;
import com.example.ecommerceproject.repository.SellerRepository;
import com.example.ecommerceproject.repository.UserRepository;
import com.example.ecommerceproject.service.AdminService;
import com.example.ecommerceproject.service.EmailService;
import com.example.ecommerceproject.service.UserSessionService;
import com.example.ecommerceproject.specs.CategorySpecification;
import com.example.ecommerceproject.specs.ProductSpecifications;
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
    final UserRepository userRepository;
    final UserSessionService userSessionService;
    final CategoryRepository categoryRepository;
    final CategoryMetadataFieldRepository metadataFieldRepository;
    final CategoryMetadataFieldValuesRepository metadataFieldValuesRepository;
    final AddressRepository addressRepository;
    final ProductRepository productRepository;
    final EmailService emailService;
    final MessageService messageService;
    final ModelMapper modelMapper;

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
    public ApiResponseDTO activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 404));

        validateUserNotDeleted(user);

        if (user.isActive()) {
            throw new ApiException(MessageKeys.VALIDATION_USER_ALREADY_ACTIVATED, 400);
        }

        user.setActive(true);
        emailService.sendAccountActivationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.ADMIN_USER_ACTIVATED), 200);
    }

    @Override
    @Transactional
    public ApiResponseDTO deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(MessageKeys.AUTH_USER_NOT_FOUND, 404));

        validateUserNotDeleted(user);
        validateNotProtectedAdmin(user);

        if (!user.isActive()) {
            throw new ApiException(MessageKeys.VALIDATION_USER_ALREADY_DEACTIVATED, 400);
        }

        user.setActive(false);

        userSessionService.revokeAllRefreshTokens(user);

        emailService.sendAccountDeactivationEmail(user.getEmail());

        return new ApiResponseDTO(messageService.get(MessageKeys.ADMIN_USER_DEACTIVATED), 200);
    }

    @Override
    @Transactional
    public ApiResponse addMetadataField(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new ApiException(MessageKeys.METADATA_FIELD_NAME_REQUIRED, 400);
        }
        String trimmedFieldName = fieldName.trim();

        if (trimmedFieldName.length() > 40) {
            throw new ApiException(MessageKeys.METADATA_FIELD_NAME_INVALID, 400);
        }
        if (metadataFieldRepository.existsByNameIgnoreCase(trimmedFieldName)) {
            throw new ApiException(MessageKeys.METADATA_FIELD_VALUE_MUST_BE_UNIQUE, 400);
        }

        CategoryMetadataField field = new CategoryMetadataField();
        field.setName(trimmedFieldName);
        Long id = metadataFieldRepository.save(field).getId();
        return new ApiResponse(messageService.get(MessageKeys.METADATA_FIELD_CREATED_SUCCESSFULLY), id);
    }

    @Override
    public Page<MetadataFieldResponseDTO> getAllMetadataFields(String query, int max, int offset, String sort,
            String order) {
        Pageable page = createPageable(max, offset, sort, order);
        Specification<CategoryMetadataField> spec = Specification
                .where(CategorySpecification.metadataFieldNameContains(query));

        return metadataFieldRepository.findAll(spec, page)
                .map(field -> {
                    return new MetadataFieldResponseDTO(field.getId(), field.getName());
                });
    }

    @Override
    @Transactional
    public ApiResponse addCategory(String categoryName, Long parentId) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new ApiException(MessageKeys.CATEGORY_NAME_REQUIRED, 400);
        }
        String trimmedCategoryName = categoryName.trim();

        if (trimmedCategoryName.length() > 40) {
            throw new ApiException(MessageKeys.CATEGORY_NAME_INVALID, 400);
        }
        if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
            throw new ApiException(messageService.get(MessageKeys.CATEGORY_NAME_MUST_BE_UNIQUE_WITHIN_PARENT), 400);
        }

        Category parent = null;
        if (parentId != null) {
            parent = categoryRepository.findById(parentId).orElseThrow(
                    () -> new ApiException(MessageKeys.INVALID_PARENT_CATEGORY_ID, 400));
            if (productRepository.existsByCategoryIdAndIsDeletedFalse(parentId)) {
                throw new ApiException(messageService.get(MessageKeys.PARENT_CANNOT_ASSOCIATE_WITH_EXISTING_PRODUCT),
                        400);
            }

            if (trimmedCategoryName.equalsIgnoreCase(parent.getName())) {
                throw new ApiException(MessageKeys.CATEGORY_NAME_CANNOT_MATCH_PARENT, 400);
            }
        }
        Category category = new Category();
        category.setName(trimmedCategoryName);
        category.setParentCategory(parent);
        Long id = categoryRepository.save(category).getId();
        return new ApiResponse(messageService.get(MessageKeys.CATEGORY_CREATED_SUCCESSFULLY), id);
    }

    @Override
    public Page<CategoryResponseDTO> getAllCategories(String query, Long categoryId, int max, int offset, String sort,
            String order) {
        Pageable pageable = createPageable(max, offset, sort, order);
        Specification<Category> spec = Specification.where(CategorySpecification.categoryNameContains(query))
                .and(CategorySpecification.categoryHasParentId(categoryId));

        return categoryRepository.findAll(spec, pageable)
                .map(field -> {
                    CategoryResponseDTO dto = new CategoryResponseDTO();
                    dto.setId(field.getId());
                    dto.setName(field.getName());
                    dto.setParentCategoryId(
                            field.getParentCategory() != null ? field.getParentCategory().getId() : null);
                    return dto;
                });
    }

    @Override
    @Transactional
    public ApiResponse updateCategory(Long categoryId, String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new ApiException(messageService.get(MessageKeys.CATEGORY_NAME_REQUIRED), 400);
        }

        String trimmedCategoryName = categoryName.trim();

        if (trimmedCategoryName.length() > 40) {
            throw new ApiException(messageService.get(MessageKeys.CATEGORY_NAME_INVALID), 400);
        }

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ApiException(messageService.get(MessageKeys.INVALID_CATEGORY_ID), 400));

        Long parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
        if (!trimmedCategoryName.equals(category.getName())) {
            if (parentId != null) {
                Category parent = category.getParentCategory();
                if (trimmedCategoryName.equalsIgnoreCase(parent.getName())
                        && categoryRepository.existsByNameIgnoreCase(categoryName)) {
                    throw new ApiException(messageService.get(MessageKeys.CATEGORY_NAME_CANNOT_MATCH_PARENT), 400);
                }
            }
        }

        category.setName(trimmedCategoryName);
        return new ApiResponse(messageService.get(MessageKeys.CATEGORY_UPDATED_SUCCESSFULLY));
    }

    @Override
    @Transactional
    public ApiResponse addCategoryMetadataFieldValues(Long categoryId,
            List<CategoryMetadataValueRequestDTO> fieldValues) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ApiException(MessageKeys.INVALID_CATEGORY_ID, 400));
        for (CategoryMetadataValueRequestDTO dto : fieldValues) {
            if (dto.getMetaDataFieldId() == null) {
                throw new ApiException(MessageKeys.INVALID_METADATA_FIELD_ID, 400);
            }
            if (dto.getValue() == null || dto.getValue().trim().isEmpty()) {
                throw new ApiException(MessageKeys.METADATA_FIELD_VALUE_REQUIRED, 400);
            }

            CategoryMetadataField metadataField = metadataFieldRepository.findById(dto.getMetaDataFieldId())
                    .orElseThrow(() -> new ApiException(MessageKeys.INVALID_METADATA_FIELD_ID, 400));

            String trimmedValue = dto.getValue().trim();

            if (metadataFieldValuesRepository.existsByCategoryIdAndMetadataFieldIdAndValue(
                    categoryId, dto.getMetaDataFieldId(), trimmedValue)) {
                throw new ApiException(MessageKeys.METADATA_FIELD_VALUE_DUPLICATE, 400);
            }
            CategoryMetadataFieldValues values = new CategoryMetadataFieldValues();
            values.setCategory(category);
            values.setMetadataField(metadataField);
            values.setValue(trimmedValue);

            try {
                metadataFieldValuesRepository.save(values);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                throw new ApiException(MessageKeys.METADATA_FIELD_VALUE_DUPLICATE, 400);
            }
        }
        return new ApiResponse(messageService.get(MessageKeys.METADATA_FIELDS_ADDED_TO_CATEGORY_SUCCESSFULLY));
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(Map<String, String> params) {
        int page = Integer.parseInt(params.getOrDefault("offset", "0"));
        int size = Integer.parseInt(params.getOrDefault("max", "10"));
        String sortBy = params.getOrDefault("sort", "id");
        Sort.Direction direction = Sort.Direction.fromString(params.getOrDefault("order", "ASC"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Specification<Product> spec = ProductSpecifications.buildFilter(params);

        return productRepository.findAll(spec, pageable)
                .map(product -> modelMapper.map(product, ProductResponseDTO.class));
    }

    @Override
    public ApiResponse toggleProductStatus(Long productId, boolean activate) {
        Product product = productRepository.findById(productId).orElseThrow(
            () -> new ApiException(MessageKeys.INVALID_PRODUCT_ID, 404)
        );

        if(activate){
            if(product.getIsActive()){
                throw new ApiException(MessageKeys.PRODUCT_ALREADY_ACTIVE, 400);
            }
            product.setIsActive(true);
            emailService.sendProductStatusEmail(product.getSeller().getUser().getEmail(), product.getName(), activate);
            return new ApiResponse(messageService.get(MessageKeys.PRODUCT_ACTIVATED_SUCCESSFULLY));
        } else {
            if(!product.getIsActive()){
                throw new ApiException(MessageKeys.PRODUCT_ALREADY_DEACTIVATED, 400);
            }
            product.setIsActive(false);
            emailService.sendProductStatusEmail(product.getSeller().getUser().getEmail(), product.getName(), activate);
            return new ApiResponse(messageService.get(MessageKeys.PRODUCT_DEACTIVATED_SUCCESSFULLY));
        }
    }

    private Pageable createPageable(int max, int offset, String sort, String order) {
        Sort.Direction direction = order.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        int pageNumber = (max > 0) ? (offset / max) : 0;
        return PageRequest.of(pageNumber, max, Sort.by(direction, sort));
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
                addr.getCountry())
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
