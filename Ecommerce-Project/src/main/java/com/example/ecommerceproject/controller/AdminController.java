package com.example.ecommerceproject.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.CustomerResponseDTO;
import com.example.ecommerceproject.dto.SellerResponseDTO;
import com.example.ecommerceproject.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin Management", description = "APIs for Administrator to manage customers and sellers")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    final AdminService adminService;

    @Operation(summary = "Get all registered customers", description = "Retrieves a paginated list of all registered customers. Can be sorted and filtered by email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of customers"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "403", description = "Access denied. Admin rights required.")
    })
    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerResponseDTO>> getCustomer(
        @Parameter(description = "Page number to retrieve (starts at 0)")
        @RequestParam(defaultValue = "0")
        @Min(value = 0, message = "{validation.page_offset_negative}") int page,

        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "{validation.page_size_min}")
        @Max(value = 100, message = "{validation.page_size_max}") int size,

        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(required = false) String email
    ){ 
        return ResponseEntity.ok(adminService.getAllCustomers(
            page, size, sort, email));
    }

    @Operation(summary = "Get all registered sellers", description = "Retrieves a paginated list of all registered sellers. Can be sorted and filtered by email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of sellers"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "403", description = "Access denied. Admin rights required.")
    })
    @GetMapping("/sellers")
    public ResponseEntity<Page<SellerResponseDTO>> getSellers(
        @Parameter(description = "Page number to retrieve (starts at 0)")
        @RequestParam(defaultValue = "0")
        @Min(value = 0, message = "{validation.page_offset_negative}") int page,

        @RequestParam(defaultValue = "10")
        @Min(value = 1, message = "{validation.page_size_min}")
        @Max(value = 100, message = "{validation.page_size_max}") int size,

        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(required = false) String email
    ){
        return ResponseEntity.ok(adminService.getAllSellers(page, size, sort, email));
    }

    @Operation(summary = "Activate a customer account", description = "Activates a currently deactivated customer account. Sends an email notification to the customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer successfully activated"),
            @ApiResponse(responseCode = "400", description = "Customer is already active or invalid ID"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PatchMapping("/customer/{id}/activate")
    public ResponseEntity<ApiResponseDTO> activateCustomer(
        @Parameter(description = "ID of the customer to activate", required = true)
        @PathVariable 
        @Positive(message = "{validation.customer_id_positive}") Long id)
    {
        return ResponseEntity.ok(adminService.activateCustomer(id));
    }

    @Operation(summary = "Deactivate a customer account", description = "Deactivates a currently active customer account. Sends an email notification to the customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer successfully deactivated"),
            @ApiResponse(responseCode = "400", description = "Customer is already deactivated, protected admin, or invalid ID"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PatchMapping("/customer/{id}/deactivate")
    public ResponseEntity<ApiResponseDTO> deactivateCustomer(
        @Parameter(description = "ID of the customer to deactivate", required = true)
        @PathVariable
        @Positive(message = "{validation.customer_id_positive}")
        Long id
    )
    {
        return ResponseEntity.ok(adminService.deactivateCustomer(id));
    }

    @Operation(summary = "Activate a seller account", description = "Activates a currently deactivated seller account. Sends an email notification to the seller.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Seller successfully activated"),
            @ApiResponse(responseCode = "400", description = "Seller is already active or invalid ID"),
            @ApiResponse(responseCode = "404", description = "Seller not found")
    })
    @PatchMapping("/seller/{id}/activate")
    public ResponseEntity<ApiResponseDTO> activateSeller(
        @Parameter(description = "ID of the seller to activate", required = true)
        @PathVariable 
        @Positive(message = "{validation.seller_id_positive}") Long id)
    {
        return ResponseEntity.ok(adminService.activateSeller(id));
    }

    @Operation(summary = "Deactivate a seller account", description = "Deactivates a currently active seller account. Sends an email notification to the seller.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Seller successfully deactivated"),
            @ApiResponse(responseCode = "400", description = "Seller is already deactivated, protected admin, or invalid ID"),
            @ApiResponse(responseCode = "404", description = "Seller not found")
    })
    @PatchMapping("/seller/{id}/deactivate")
    public ResponseEntity<ApiResponseDTO> deactivateSeller(
        @Parameter(description = "ID of the seller to deactivate", required = true)
        @PathVariable
        @Positive(message = "{validation.customer_id_positive}")
        Long id
    )
    {
        return ResponseEntity.ok(adminService.deactivateSeller(id));
    }

}
