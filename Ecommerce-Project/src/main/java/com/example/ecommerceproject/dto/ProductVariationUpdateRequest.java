package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ProductVariationUpdateRequest {
    @Min(value = 0, message = "{validation.quantity_cannot_be_less_than_zero}")
    Integer quantityAvailable;

    @Min(value = 0, message = "{validation.price_cannot_be_less_than_zero}")
    BigDecimal price;

    Map<String, String> metadata;
    
    @Pattern(regexp = "(?i).*\\.(jpg|png)$", message = "{validation.image_valid}")
    String primaryImageUrl;

    List<String> secondaryImageUrls;
    Boolean isActive;
}
