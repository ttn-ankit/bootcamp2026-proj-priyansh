package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import com.example.ecommerceproject.enums.AddressLabelEnums;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Schema(description = "Seller registration request")
@FieldDefaults(level = PRIVATE)
public class SellerRegisterRequestDTO {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is mandatory")
    @Schema(example = "seller@example.com", description = "Seller email address")
    String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(example = "Password@123", description = "Seller account password")
    String password;

    @NotBlank(message = "Confirm password is mandatory")
    @Schema(example = "Password@123", description = "Confirm password")
    String confirmPassword;

    @NotBlank(message = "GST number is required")
    @Pattern(
        regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[A-Z0-9]{3}$",
        message = "Invalid GST format"
    )
    @Schema(example = "22AAAAA0000A1Z5", description = "GST number of the seller")
    String gst;

    @NotBlank(message = "Company name is required")
    @Schema(example = "ABC Electronics Pvt Ltd", description = "Seller company name")
    String companyName;

    @NotBlank(message = "Company contact is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number")
    @Schema(example = "9876543210", description = "Company contact number")
    String companyContact;

    @NotBlank(message = "First name is required")
    @Schema(example = "Priyansh", description = "Seller first name")
    String firstName;

    @Schema(example = "Kumar")
    @Size(max = 30)
    String middleName;

    @NotBlank(message = "Last name is required")
    @Schema(example = "Awasthi", description = "Seller last name")
    String lastName;

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