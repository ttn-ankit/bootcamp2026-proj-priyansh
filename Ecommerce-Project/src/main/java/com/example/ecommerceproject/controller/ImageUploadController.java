package com.example.ecommerceproject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.service.ImageUploadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER')")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Image Management", description = "Common APIs for both Customers and Sellers to manage profile images")
@SecurityRequirement(name = "bearerAuth")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @Operation(summary = "Upload Profile Image", 
               description = "Upload a profile image for the user. Supports JPG, JPEG, PNG formats. Maximum file size: 5MB")
    @PostMapping("/{userId}/image")
    public ResponseEntity<ApiResponseDTO> uploadImage(
            @Parameter(description = "User ID", required = true)
            @PathVariable 
            @Positive(message = "{validation.invalid_id_format}")
            Long userId,
            
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("image") 
            MultipartFile file) {
        
        return ResponseEntity.ok(imageUploadService.uploadUserImage(userId, file));
    }
}