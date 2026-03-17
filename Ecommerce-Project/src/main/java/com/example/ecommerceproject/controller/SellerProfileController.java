package com.example.ecommerceproject.controller;

import com.example.ecommerceproject.dto.*;
import com.example.ecommerceproject.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('SELLER')")
@RequiredArgsConstructor
@Tag(name = "Seller Profile Management", description = "APIs for Sellers to manage their profile, password, and addresses")
@SecurityRequirement(name = "bearerAuth")
public class SellerProfileController {

    private final SellerService sellerService;

    @Operation(summary = "View Seller Profile", description = "Retrieves the complete profile data of the currently logged-in seller.")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<SellerProfileResponseDTO> getProfile(
            @PathVariable Long userId) {
        return ResponseEntity.ok(sellerService.getProfile(userId));
    }

    @Operation(summary = "Update Seller Profile", description = "Updates basic profile information (first name, last name, company details) for the logged-in seller.")
    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ApiResponseDTO> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody SellerProfileUpdateRequestDTO dto) {
        return ResponseEntity.ok(sellerService.updateProfile(userId, dto));
    }

    @Operation(summary = "Update Password", description = "Updates the password for the logged-in seller. Enforces password complexity rules.")
    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponseDTO> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordUpdateRequestDTO dto) {
        return ResponseEntity.ok(sellerService.updatePassword(userId, dto));
    }

    @Operation(summary = "Update Address", description = "Updates the seller's address (sellers can only have one address).")
    @PatchMapping("/{userId}/address")
    public ResponseEntity<ApiResponseDTO> updateAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressPartialUpdateRequestDTO dto) {
        return ResponseEntity.ok(sellerService.updateAddress(userId, dto));
    }
}
