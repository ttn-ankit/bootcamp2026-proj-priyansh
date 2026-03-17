package com.example.ecommerceproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.ecommerceproject.entity.CategoryMetadataField;

public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField, Long>, JpaSpecificationExecutor<CategoryMetadataField>{
    boolean existsByNameIgnoreCase(String name);
}
