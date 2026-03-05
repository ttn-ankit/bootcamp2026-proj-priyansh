package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.ecommerceproject.enums.AddressLabelEnums;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,15}$", message = "Password must be 8-15 chars with upper, lower, number & special char")
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

    @Schema(
        description = "City where the user resides",
        example = "Delhi",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "City cannot be empty")
    @Size(min = 2, max = 20, message = "City must be between 2 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "City must contain only letters")
    String city;

    @Schema(
        description = "State of the address",
        example = "Uttar Pradesh",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "State cannot be empty")
    @Size(min = 2, max = 20, message = "State must be between 2 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "State must contain only letters")
    String state;

    @Schema(
        description = "Country name",
        example = "India",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Country cannot be empty")
    @Size(min = 2, max = 20, message = "Country must be between 2 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Country must contain only letters")
    String country;

    @Schema(
        description = "Complete address line including house number, street, and locality",
        example = "221B Baker Street",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Address line cannot be empty")
    @Size(min = 5, max = 50, message = "Address must be between 5 and 50 characters")
    String addressLine;

    @Schema(
        description = "Postal / ZIP code of the address",
        example = "110001",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Zip code cannot be empty")
    @Pattern(regexp = "^[0-9]{6}$", message = "Zip code must be a valid 6 digit code")
    String zipCode;

    @Schema(
        description = "Label used to categorize the address",
        example = "HOME",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Address label is required")
    AddressLabelEnums label;

}
