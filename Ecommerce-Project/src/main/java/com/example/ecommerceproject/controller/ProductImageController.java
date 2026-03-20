package com.example.ecommerceproject.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Image Management", description = "APIs for managing product images")
@SecurityRequirement(name = "bearerAuth")
public class ProductImageController {

    private final ImageUploadService imageUploadService;

    @Operation(summary = "Upload Product Primary Image", 
               description = "Upload primary image for a product. Only product owner (seller) or admin can upload. Supports JPG, JPEG, PNG formats. Maximum file size: 5MB")
    @PostMapping("/seller/products/{productId}/images/primary")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> uploadPrimaryImage(
            @Parameter(description = "Product ID", required = true)
            @PathVariable 
            @Positive(message = "{validation.invalid_id_format}")
            Long productId,
            
            @Parameter(description = "Primary image file to upload", required = true)
            @RequestParam("image") 
            MultipartFile file) {
        
        return ResponseEntity.ok(imageUploadService.uploadProductPrimaryImage(productId, file));
    }

    @Operation(summary = "Upload Product Secondary Images", 
               description = "Upload secondary images for a product. Only product owner (seller) or admin can upload. Supports JPG, JPEG, PNG formats. Maximum file size: 5MB per file")
    @PostMapping("/seller/products/{productId}/images/secondary")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO> uploadSecondaryImages(
            @Parameter(description = "Product ID", required = true)
            @PathVariable 
            @Positive(message = "{validation.invalid_id_format}")
            Long productId,
            
            @Parameter(description = "Secondary image files to upload", required = true)
            @RequestParam("images") 
            MultipartFile[] files) {
        
        return ResponseEntity.ok(imageUploadService.uploadProductSecondaryImages(productId, files));
    }

    @Operation(summary = "Get Product Primary Image", 
               description = "Retrieve product primary image. Public access - no authentication required.")
    @GetMapping("/products/{productId}/images/primary/{filename}")
    public ResponseEntity<Resource> getProductPrimaryImage(
            @Parameter(description = "Product ID", required = true)
            @PathVariable 
            @Positive(message = "{validation.invalid_id_format}")
            Long productId,
            
            @Parameter(description = "Image filename", required = true)
            @PathVariable 
            String filename) {
        
        Resource resource = imageUploadService.getProductPrimaryImage(productId, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    @Operation(summary = "Get Product Secondary Image", 
               description = "Retrieve product secondary image. Public access - no authentication required.")
    @GetMapping("/products/{productId}/images/secondary/{filename}")
    public ResponseEntity<Resource> getProductSecondaryImage(
            @Parameter(description = "Product ID", required = true)
            @PathVariable 
            @Positive(message = "{validation.invalid_id_format}")
            Long productId,
            
            @Parameter(description = "Image filename", required = true)
            @PathVariable 
            String filename) {
        
        Resource resource = imageUploadService.getProductSecondaryImage(productId, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    @Operation(summary = "Get Product Secondary Image Filenames", 
               description = "Get list of secondary image filenames for a product. Public access - no authentication required.")
    @GetMapping("/products/{productId}/images/secondary")
    public ResponseEntity<List<String>> getProductSecondaryImageFilenames(
            @Parameter(description = "Product ID", required = true)
            @PathVariable 
            @Positive(message = "{validation.invalid_id_format}")
            Long productId) {
        
        List<String> filenames = imageUploadService.getProductSecondaryImageFilenames(productId);
        return ResponseEntity.ok(filenames);
    }
}