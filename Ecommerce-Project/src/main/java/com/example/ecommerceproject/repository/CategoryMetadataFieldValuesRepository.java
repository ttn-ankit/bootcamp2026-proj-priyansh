package com.example.ecommerceproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.CategoryMetadataFieldValues;

@Repository
public interface CategoryMetadataFieldValuesRepository extends JpaRepository<CategoryMetadataFieldValues, Long>{

}
