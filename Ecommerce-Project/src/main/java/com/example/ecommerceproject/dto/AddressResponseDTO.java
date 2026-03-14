package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AddressResponseDTO {
    Long id;
    String addressLine;
    String city;
    String state;
    String country;
    String zipCode;
    String label;
}
