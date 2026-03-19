package com.example.ecommerceproject.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CategoryMetadataValueRequestDTO {
    @NotNull(message = "{category.invalid_metadata_field_id}")
    Long metaDataFieldId;
    
    @NotBlank(message = "{category.metadata_field_value_required}")
    String value;   
}
