package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;
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
    @Size(min = 8, max = 25, message = "Password must be 8-25 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$", message = "Password must be 8-25 chars with at least one lower, upper, digit and special char")
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

    @NotNull
    @Schema(description = "Address")
    AddressDTO address;

}
