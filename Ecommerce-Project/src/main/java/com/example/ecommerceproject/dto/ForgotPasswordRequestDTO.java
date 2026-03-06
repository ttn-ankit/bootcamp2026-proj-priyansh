package com.example.ecommerceproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequestDTO {

    @Schema(example = "user@example.com", description = "Registered email address")
    @NotBlank
    @Email(message = "Invalid Email")
    private String email;
}

