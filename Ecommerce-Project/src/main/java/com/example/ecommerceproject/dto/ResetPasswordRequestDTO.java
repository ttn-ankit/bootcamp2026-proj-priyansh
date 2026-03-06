package com.example.ecommerceproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {

    @Schema(description = "Password reset token from email link")
    @NotBlank
    private String token;

    @Schema(example = "Password@123", description = "New password")
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,15}$",
            message = "Password must be 8-15 chars with upper, lower, number & special char")
    private String password;

    @Schema(example = "Password@123", description = "Confirm new password")
    @NotBlank
    private String confirmPassword;
}

