package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;
<<<<<<< HEAD
=======

import com.example.ecommerceproject.enums.AddressLabelEnums;

>>>>>>> bdb0356 (Refactored)
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

<<<<<<< HEAD
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
=======
    @Email(message = "{validation.email_invalid}")
    @NotBlank(message = "{validation.email_required}")
    @Schema(example = "seller@example.com", description = "Seller email address")
    String email;

    @NotBlank(message = "{validation.password_required}")
    @Size(min = 8, max = 25, message = "{validation.password_length}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,25}$", message = "{validation.password_strength}")
    @Schema(example = "Password@123", description = "Seller account password")
    String password;

    @NotBlank(message = "{validation.confirm_password_required}")
    @Schema(example = "Password@123", description = "Confirm password")
    String confirmPassword;

    @NotBlank(message = "{validation.gst_required}")
    @Pattern(
        regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[A-Z0-9]{3}$",
        message = "{validation.gst_invalid}"
>>>>>>> bdb0356 (Refactored)
    )
    @Schema(example = "22AAAAA0000A1Z5", description = "GST number of the seller")
    String gst;

<<<<<<< HEAD
    @NotBlank(message = "Company name is required")
    @Schema(example = "ABC Electronics Pvt Ltd", description = "Seller company name")
    String companyName;

    @NotBlank(message = "Company contact is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number")
    @Schema(example = "9876543210", description = "Company contact number")
    String companyContact;

    @NotBlank(message = "First name is required")
=======
    @NotBlank(message = "{validation.company_name_required}")
    @Schema(example = "ABC Electronics Pvt Ltd", description = "Seller company name")
    String companyName;

    @NotBlank(message = "{validation.company_contact_required}")
    @Pattern(regexp = "^[0-9]{10}$", message = "{validation.phone_invalid}")
    @Schema(example = "9876543210", description = "Company contact number")
    String companyContact;

    @NotBlank(message = "{validation.first_name_required}")
>>>>>>> bdb0356 (Refactored)
    @Schema(example = "Priyansh", description = "Seller first name")
    String firstName;

    @Schema(example = "Kumar")
    @Size(max = 30)
    String middleName;

<<<<<<< HEAD
    @NotBlank(message = "Last name is required")
    @Schema(example = "Awasthi", description = "Seller last name")
    String lastName;

    @NotNull
    @Schema(description = "Address")
    AddressDTO address;

}
=======
    @NotBlank(message = "{validation.last_name_required}")
    @Schema(example = "Awasthi", description = "Seller last name")
    String lastName;

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
}
>>>>>>> bdb0356 (Refactored)
