package com.example.ecommerceproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerceproject.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{

}
