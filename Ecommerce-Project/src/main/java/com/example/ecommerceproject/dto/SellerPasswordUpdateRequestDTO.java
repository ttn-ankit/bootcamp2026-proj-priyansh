package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class SellerPasswordUpdateRequestDTO {

    @NotBlank(message = "{validation.password_required}")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$",
        message = "{validation.password_strength}")
    String password;

    @NotBlank(message = "{validation.confirm_password_required}")
    String confirmPassword;
}

