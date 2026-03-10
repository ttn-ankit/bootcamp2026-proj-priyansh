package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
=======

import com.example.ecommerceproject.enums.AddressLabelEnums;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
>>>>>>> bdb0356 (Refactored)
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
<<<<<<< HEAD
    @Email(message = "Invalid Email")
=======
    @Email(message = "{validation.email_invalid}")
>>>>>>> bdb0356 (Refactored)
    String email;

    @Schema(example = "Password@123", description = "User password")
    @NotBlank
<<<<<<< HEAD
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$", message = "Password must be 8-25 chars with at least one lower, upper, digit and special char")
=======
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$", message = "{validation.password_strength}")
>>>>>>> bdb0356 (Refactored)
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
<<<<<<< HEAD
    @Pattern(regexp="^[0-9]{10}$", message="Invalid phone number")
    String phoneNumber;
=======
    @Pattern(regexp="^[0-9]{10}$", message="{validation.phone_invalid}")
    String phoneNumber;

    @Schema(
        description = "City where the user resides",
        example = "Delhi",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.city_required}")
    @Size(min = 2, max = 20, message = "{validation.city_length}")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "{validation.city_invalid}")
    String city;

    @Schema(
        description = "State of the address",
        example = "Uttar Pradesh",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.state_required}")
    @Size(min = 2, max = 20, message = "{validation.state_length}")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "{validation.state_invalid}")
    String state;

    @Schema(
        description = "Country name",
        example = "India",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.country_required}")
    @Size(min = 2, max = 20, message = "{validation.country_length}")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "{validation.country_invalid}")
    String country;

    @Schema(
        description = "Complete address line including house number, street, and locality",
        example = "221B Baker Street",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.address_line_required}")
    @Size(min = 5, max = 50, message = "{validation.address_line_invalid}")
    String addressLine;

    @Schema(
        description = "Postal / ZIP code of the address",
        example = "110001",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{validation.zip_code_required}")
    @Pattern(regexp = "^[0-9]{6}$", message = "{validation.zip_code_invalid}")
    String zipCode;

    @Schema(
        description = "Label used to categorize the address",
        example = "HOME",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "{validation.address_label_required}")
    AddressLabelEnums label;

>>>>>>> bdb0356 (Refactored)
}
