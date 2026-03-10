package com.example.ecommerceproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressUpdateRequestDTO {
    @NotBlank(message = "AddressLine is required")
    String addressLine;

    @NotBlank(message = "City is required")
    String city;

    @NotBlank(message = "State is required")
    String state;

    @NotBlank(message = "Country is required")
    String country;

    @NotBlank(message = "Zip Code is required")
    String zipCode;
}
