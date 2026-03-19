package com.example.ecommerceproject.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerceproject.dto.ApiResponseDTO;

public interface ImageUploadService {
    ApiResponseDTO uploadUserImage(Long userId, MultipartFile file);
}