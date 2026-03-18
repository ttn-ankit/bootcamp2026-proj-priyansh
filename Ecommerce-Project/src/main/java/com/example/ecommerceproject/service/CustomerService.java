package com.example.ecommerceproject.service;

import java.util.List;

import com.example.ecommerceproject.dto.AddressDTO;
import com.example.ecommerceproject.dto.AddressPartialUpdateRequestDTO;
import com.example.ecommerceproject.dto.AddressResponseDTO;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CategoryFilterDetailsDTO;
import com.example.ecommerceproject.dto.CategoryResponseDTO;
import com.example.ecommerceproject.dto.CustomerProfileResponseDTO;
import com.example.ecommerceproject.dto.CustomerProfileUpdateRequestDTO;
import com.example.ecommerceproject.dto.PasswordUpdateRequestDTO;

public interface CustomerService {
    CustomerProfileResponseDTO getProfile(Long userId);
    ApiResponseDTO updateProfile(Long userId, CustomerProfileUpdateRequestDTO dto);
    ApiResponseDTO updatePassword(Long userId, PasswordUpdateRequestDTO dto);
    List<AddressResponseDTO> getAddresses(Long userId);
    ApiResponseDTO addAddress(Long userId, AddressDTO dto);
    ApiResponseDTO updateAddress(Long userId, Long addressId, AddressPartialUpdateRequestDTO dto);
    ApiResponseDTO deleteAddress(Long userId, Long addressId); 
    List<CategoryResponseDTO> getCategories(Long categoryId);
    CategoryFilterDetailsDTO getCategoryFilteringDetails(Long categoryId);
}
