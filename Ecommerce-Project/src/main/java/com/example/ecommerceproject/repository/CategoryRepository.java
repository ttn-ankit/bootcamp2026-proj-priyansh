package com.example.ecommerceproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category>{
    boolean existsByNameAndParentCategoryIsNull(String name);
    boolean existsByNameAndParentCategory(String name, Long parentId);
    @Query("select c from Category c where SIZE(c.subCategories) = 0")
    List<Category> findAllLeafNodes();
    List<Category> findByParentCategoryIsNull();
    List<Category> findByParentCategoryId(Long parentId);
}
