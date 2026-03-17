package com.example.ecommerceproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.ecommerceproject.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category>{
    boolean existsByNameAndParentCategoryIsNull(String name);
    boolean existsByNameAndParentCategory(String name, Long parentId);
}
