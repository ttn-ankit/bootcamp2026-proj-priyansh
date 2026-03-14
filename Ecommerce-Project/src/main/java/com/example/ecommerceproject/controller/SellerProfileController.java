package com.example.ecommerceproject.controller;

import com.example.ecommerceproject.dto.*;
import com.example.ecommerceproject.service.SellerService;
import com.example.ecommerceproject.service.impl.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @GetMapping("/profile")
    public ResponseEntity<SellerProfileResponseDTO> getMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(sellerService.getProfile(userDetails.getUserId()));
    }

    @Operation(summary = "Update Seller Profile", description = "Updates basic profile information (first name, last name, company details) for the logged-in seller.")
    @PatchMapping("/update")
    public ResponseEntity<ApiResponseDTO> updateMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SellerProfileUpdateRequestDTO dto) {
        return ResponseEntity.ok(sellerService.updateProfile(userDetails.getUserId(), dto));
    }

    @Operation(summary = "Update Password", description = "Updates the password for the logged-in seller. Enforces password complexity rules.")
    @PatchMapping("/password")
    public ResponseEntity<ApiResponseDTO> updateMyPassword(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordUpdateRequestDTO dto) {
        return ResponseEntity.ok(sellerService.updatePassword(userDetails.getUserId(), dto));
    }

    @Operation(summary = "Update Address", description = "Updates the seller's address (sellers can only have one address).")
    @PatchMapping("/address")
    public ResponseEntity<ApiResponseDTO> updateMyAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddressPartialUpdateRequestDTO dto) {
        return ResponseEntity.ok(sellerService.updateAddress(userDetails.getUserId(), dto));
    }
}