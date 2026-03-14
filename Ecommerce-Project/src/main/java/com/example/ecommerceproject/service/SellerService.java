package com.example.ecommerceproject.service;

import com.example.ecommerceproject.dto.AddressPartialUpdateRequestDTO;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.PasswordUpdateRequestDTO;
import com.example.ecommerceproject.dto.SellerProfileResponseDTO;
import com.example.ecommerceproject.dto.SellerProfileUpdateRequestDTO;

public interface SellerService {
    SellerProfileResponseDTO getProfile(Long userId);
    ApiResponseDTO updateProfile(Long userId, SellerProfileUpdateRequestDTO dto);
    ApiResponseDTO updatePassword(Long userId, PasswordUpdateRequestDTO dto);
    ApiResponseDTO updateAddress(Long userId, AddressPartialUpdateRequestDTO dto);
}
