package com.example.ecommerceproject.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

import io.swagger.v3.oas.annotations.Operation;
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
    @GetMapping("/{userId}/profile")
    public ResponseEntity<CustomerProfileResponseDTO> getProfile(
        @PathVariable Long userId
    ){
        return ResponseEntity.ok(customerService.getProfile(userId));
    }

    @Operation(summary = "Update Profile", description = "Partial update of customer profile fields")
    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ApiResponseDTO> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody CustomerProfileUpdateRequestDTO dto) {
        return ResponseEntity.ok(customerService.updateProfile(userId, dto));
    }

    @Operation(summary = "Update Password")
    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponseDTO> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordUpdateRequestDTO dto) {
        return ResponseEntity.ok(customerService.updatePassword(userId, dto));
    }

    @Operation(summary = "View All Addresses")
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressResponseDTO>> getAddresses(
            @PathVariable Long userId) {
        return ResponseEntity.ok(customerService.getAddresses(userId));
    }

    @Operation(summary = "Add New Address")
    @PostMapping("/{userId}/addresses")
    public ResponseEntity<ApiResponseDTO> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressDTO dto) {
        return ResponseEntity.ok(customerService.addAddress(userId, dto));
    }

    @Operation(summary = "Update Address", description = "Partial update of a specific address")
    @PatchMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<ApiResponseDTO> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressPartialUpdateRequestDTO dto) {
        return ResponseEntity.ok(customerService.updateAddress(userId, addressId, dto));
    }

    @Operation(summary = "Delete Address", description = "Soft deletes a specific address")
    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<ApiResponseDTO> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        return ResponseEntity.ok(customerService.deleteAddress(userId, addressId));
    }
}
