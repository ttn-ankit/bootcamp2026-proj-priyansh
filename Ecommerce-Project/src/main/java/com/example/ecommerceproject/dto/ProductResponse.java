package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    String brand;
    Long categoryId;
    String description;
    Boolean isCancellable;
    Boolean isReturnable;
    Boolean isActive;
}
