package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class ResetPasswordRequestDTO {

    @Schema(description = "Password reset token from email link")
    @NotBlank
    String token;

    @Schema(example = "Password@123", description = "New password")
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$",
            message = "Password must be 8-25 chars with at least one lower, upper, digit and special char")
    String password;

    @Schema(example = "Password@123", description = "Confirm new password")
    @NotBlank
    String confirmPassword;
}

