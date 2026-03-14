package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level =  PRIVATE)
public class CustomerProfileUpdateRequestDTO {
    String firstName;
    String lastName;
    String contact;
}
