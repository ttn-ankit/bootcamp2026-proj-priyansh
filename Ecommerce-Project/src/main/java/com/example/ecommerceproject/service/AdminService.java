package com.example.ecommerceproject.service;

import org.springframework.data.domain.Page;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CustomerResponseDTO;
import com.example.ecommerceproject.dto.SellerResponseDTO;

public interface AdminService{
    Page<CustomerResponseDTO> getAllCustomers(int page, int size, String sort, String email);
    Page<SellerResponseDTO> getAllSellers(int page, int size, String sort, String email);

    ApiResponseDTO activateCustomer(Long customerId);
    ApiResponseDTO deactivateCustomer(Long customerId);

    ApiResponseDTO activateSeller(Long sellerId);
    ApiResponseDTO deactivateSeller(Long sellerId);
}