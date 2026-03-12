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
    @NotBlank(message = "{validation.email_required}")
    @Email(message = "{validation.email_invalid}")
    String email;

    @Schema(example = "Password@123", description = "User password")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$", message = "{validation.password_strength}")
    @NotBlank(message = "{validation.password_required}")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$",
        message = "{validation.password_strength}")
    String password;

    @Schema(example = "Password@123", description = "User confirmed password")
    @NotBlank(message = "{validation.confirm_password_required}")
    String confirmPassword;

    @Schema(example = "John")
    @NotBlank(message = "{validation.first_name_required}")
    @Size(max = 30)
    String firstName;

    @Schema(example = "Martin")
    @Size(max = 30)
    String middleName;

    @Schema(example = "Doe")
    @NotBlank(message = "{validation.last_name_required}")
    @Size(max = 30)
    String lastName;

    @Pattern(regexp="^[0-9]{10}$", message="{validation.phone_invalid}")
    @NotBlank(message = "{validation.phone_invalid}")
    @Pattern(regexp = "^[0-9]{10}$", message = "{validation.phone_invalid}")
    String phoneNumber;
}
