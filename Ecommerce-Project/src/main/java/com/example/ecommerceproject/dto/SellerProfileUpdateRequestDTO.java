package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class SellerProfileUpdateRequestDTO {
    @NotBlank(message = "First name is required")
    String firstName;

    String middleName;
    String lastName;

    @NotBlank(message = "Comapny Name is required")
    String companyName;

    @NotBlank(message = "Company Contact is required")
    String companyContact;
}
