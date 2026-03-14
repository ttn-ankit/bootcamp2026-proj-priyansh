package com.example.ecommerceproject.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceproject.dto.AddressDTO;
import com.example.ecommerceproject.dto.AddressPartialUpdateRequestDTO;
import com.example.ecommerceproject.dto.AddressResponseDTO;
import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CustomerProfileResponseDTO;
import com.example.ecommerceproject.dto.CustomerProfileUpdateRequestDTO;
import com.example.ecommerceproject.dto.PasswordUpdateRequestDTO;
import com.example.ecommerceproject.service.CustomerService;
import com.example.ecommerceproject.service.impl.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Tag(name = "Customer Profile Management", description = "APIs for Customers to manage their profile and multiple addresses")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    final CustomerService customerService;

    @Operation(summary = "View Customer Profile")
    @GetMapping
    public ResponseEntity<CustomerProfileResponseDTO> getMyprofile(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return ResponseEntity.ok(customerService.getProfile(userDetails.getUserId()));
    } 

    @Operation(summary = "Update Profile", description = "Partial update of customer profile fields")
    @PatchMapping
    public ResponseEntity<ApiResponseDTO> updateMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CustomerProfileUpdateRequestDTO dto) {
        return ResponseEntity.ok(customerService.updateProfile(userDetails.getUserId(), dto));
    }

    @Operation(summary = "Update Password")
    @PatchMapping("/password")
    public ResponseEntity<ApiResponseDTO> updateMyPassword(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordUpdateRequestDTO dto) {
        return ResponseEntity.ok(customerService.updatePassword(userDetails.getUserId(), dto));
    }

    @Operation(summary = "View All Addresses")
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressResponseDTO>> getMyAddresses(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(customerService.getAddresses(userDetails.getUserId()));
    }

    @Operation(summary = "Add New Address")
    @PostMapping("/addresses")
    public ResponseEntity<ApiResponseDTO> addAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddressDTO dto) {
        return ResponseEntity.ok(customerService.addAddress(userDetails.getUserId(), dto));
    }

    @Operation(summary = "Update Address", description = "Partial update of a specific address")
    @PatchMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponseDTO> updateAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressPartialUpdateRequestDTO dto) {
        return ResponseEntity.ok(customerService.updateAddress(userDetails.getUserId(), addressId, dto));
    }

    @Operation(summary = "Delete Address", description = "Soft deletes a specific address")
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponseDTO> deleteAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId) {
        return ResponseEntity.ok(customerService.deleteAddress(userDetails.getUserId(), addressId));
    }
}
