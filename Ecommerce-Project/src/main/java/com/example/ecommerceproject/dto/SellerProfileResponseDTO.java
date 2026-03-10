package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class SellerProfileResponseDTO {
    Long id;
    String firstName;
    String middleName;
    String lastName;
    boolean isActive;
    String companyContact;
    String companyName;
    String image;
    String gst;
    Long addressId;
    String addressLine;
    String city;
    String state;
    String country;
    String zipCode;
}
