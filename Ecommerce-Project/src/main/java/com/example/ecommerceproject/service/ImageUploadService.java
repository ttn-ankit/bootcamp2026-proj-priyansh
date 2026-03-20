package com.example.ecommerceproject.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerceproject.dto.ApiResponseDTO;

public interface ImageUploadService {
    ApiResponseDTO uploadUserImage(Long userId, MultipartFile file);
    Resource getUserImage(Long userId, String filename);
    
    ApiResponseDTO uploadProductPrimaryImage(Long productId, MultipartFile file);
    ApiResponseDTO uploadProductSecondaryImages(Long productId, MultipartFile[] files);
    Resource getProductPrimaryImage(Long productId, String filename);
    Resource getProductSecondaryImage(Long productId, String filename);
    List<String> getProductSecondaryImageFilenames(Long productId);
}