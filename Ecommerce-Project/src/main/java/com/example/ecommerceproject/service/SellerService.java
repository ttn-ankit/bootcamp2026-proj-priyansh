package com.example.ecommerceproject.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.ecommerceproject.dto.AddressPartialUpdateRequestDTO;
import com.example.ecommerceproject.dto.ApiResponse;
import com.example.ecommerceproject.dto.ApiResponseDTO;
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

public interface SellerService {
    SellerProfileResponseDTO getProfile(Long userId);
    ApiResponseDTO updateProfile(Long userId, SellerProfileUpdateRequestDTO dto);
    ApiResponseDTO updatePassword(Long userId, PasswordUpdateRequestDTO dto);
    ApiResponseDTO updateAddress(Long userId, AddressPartialUpdateRequestDTO dto);
    List<SellerCategoryResponseDTO> getAllLeafCategories();
    ApiResponse createProduct(Long sellerId, ProductCreateRequest dto);
    ApiResponse createProductVariation(Long sellerId, Long productId, ProductVariationCreateRequest dto);
    Page<ProductResponse> getAllProducts(Long sellerId, int offset, int max, String sort, String order);
    Page<ProductVariationResponse> getProductVariations(Long sellerId, Long productId, int offset, int max, String sort, String order);
    ApiResponse deleteProduct(Long sellerId, Long productId);
    ApiResponse updateProduct(Long sellerId, Long productId, ProductUpdateRequest dto);
    ApiResponse updateProductVariation(Long sellerId, Long productId, Long variationId, ProductVariationUpdateRequest dto);
}
