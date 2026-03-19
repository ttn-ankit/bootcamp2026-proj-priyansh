package com.example.ecommerceproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    @Query("select distinct p.brand from Product p where p.category.id IN :categoryIds AND p.brand IS NOT NULL")
    List<String> findDistinctBrandsByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    @Query("select MIN(pv.price) from ProductVariations pv where pv.product.category.id IN :categoryIds AND pv.isActive = true")
    Double findMinPriceByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT MAX(pv.price) FROM ProductVariations pv WHERE pv.product.category.id IN :categoryIds AND pv.isActive = true")
    Double findMaxPriceByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    boolean existsByCategoryIdAndIsDeletedFalse(Long categoryId);
}
