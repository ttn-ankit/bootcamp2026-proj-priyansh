package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class SellerPasswordUpdateRequestDTO {

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$", 
            message = "Password must be 8-15 characters with at least 1 lowercase, 1 uppercase, 1 number, and 1 special character")
    String password;

    String confirmPassword;
}

