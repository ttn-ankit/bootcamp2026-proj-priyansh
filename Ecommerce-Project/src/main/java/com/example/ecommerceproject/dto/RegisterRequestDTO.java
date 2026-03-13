package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class RegisterRequestDTO {

    @Schema(example = "user@example.com", description = "User email address")
    @NotBlank
    @Email(message = "Invalid Email")
    String email;

    @Schema(example = "Password@123", description = "User password")
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$", message = "Password must be 8-25 chars with at least one lower, upper, digit and special char")
    String password;


    @Schema(example = "Password@123", description = "User confirmed password")
    @NotBlank
    String confirmPassword;

    @Schema(example = "John")
    @NotBlank
    @Size(max = 30)
    String firstName;

    @Schema(example = "Martin")
    @NotBlank
    @Size(max = 30)
    String middleName;

    @Schema(example = "Doe")
    @NotBlank
    @Size(max = 30)
    String lastName;

    @Schema(example = "9876543210")
    @NotBlank
    @Pattern(regexp="^[0-9]{10}$", message="Invalid phone number")
    String phoneNumber;
}
