package com.example.ecommerceproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressUpdateRequestDTO {

    @NotBlank(message = "{validation.address_line_required}")
    String addressLine;

    @NotBlank(message = "{validation.city_required}")
    String city;

    @NotBlank(message = "{validation.state_required}")
    String state;

    @NotBlank(message = "{validation.country_required}")
    String country;

    @NotBlank(message = "{validation.zip_code_required}")
    String zipCode;
}
