package com.example.ecommerceproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommerceproject.entity.Seller;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long>{
    boolean existsByGstIgnoreCase(String email);
}
