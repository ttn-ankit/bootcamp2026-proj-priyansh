package com.example.ecommerceproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerceproject.entity.Address;
import com.example.ecommerceproject.entity.User;

public interface AddressRepository extends JpaRepository<Address, Long>{
    List<Address> findByUserAndUserIsDeletedFalse(User user);
    Optional<Address> findByIdAndIsDeletedFalse(Long id);
    long countByUser(User user);
}
