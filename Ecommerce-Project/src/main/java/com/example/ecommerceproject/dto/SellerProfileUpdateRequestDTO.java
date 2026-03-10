package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class SellerProfileUpdateRequestDTO {

    @NotBlank(message = "{validation.first_name_required}")
    String firstName;

    String middleName;

    @NotBlank(message = "{validation.last_name_required}")
    String lastName;

    @NotBlank(message = "{validation.company_name_required}")
    String companyName;

    @NotBlank(message = "{validation.company_contact_required}")
    @Pattern(regexp = "^[0-9]{10}$", message = "{validation.phone_invalid}")
    String companyContact;
}
