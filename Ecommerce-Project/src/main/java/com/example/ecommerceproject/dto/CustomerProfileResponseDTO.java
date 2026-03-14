package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CustomerProfileResponseDTO {
    Long id;
    String firstName;
    String lastName;
    boolean isActive;
    String contact;
    String image;
}
