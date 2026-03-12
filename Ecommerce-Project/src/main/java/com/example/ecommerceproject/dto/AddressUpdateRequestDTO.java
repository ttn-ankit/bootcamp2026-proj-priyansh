package com.example.ecommerceproject.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.example.ecommerceproject.enums.AddressType;

@Data
public class AddressUpdateRequestDTO {

    @Size(max = 255, message = "{validation.address_line_invalid}")
    String addressLine;

    @Size(max = 100, message = "{validation.city_invalid}")
    String city;

    @Size(max = 100, message = "{validation.state_invalid}")
    String state;

    @Size(max = 100, message = "{validation.country_invalid}")
    String country;

    @Size(max = 20, message = "{validation.zip_code_invalid}")
    String zipCode;
    
    @NotNull(message = "{validation.address_label_required}")
    AddressType label;
}
