package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class SellerResponseDTO {    
    Long id;
    String fullName;
    String email;
    boolean isActive;
    String companyName;
    String companyContact;
    String companyAddress;
}
