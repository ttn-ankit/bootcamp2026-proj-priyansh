package com.example.ecommerceproject.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerceproject.entity.ProductVariations;

public interface ProductVariationRepository extends JpaRepository<ProductVariations, Long>{
    Page<ProductVariations> findAllByProductId(Long productId, Pageable pageable);

    Optional<ProductVariations> findByIdAndProductId(Long variationId, Long productId);
}   
