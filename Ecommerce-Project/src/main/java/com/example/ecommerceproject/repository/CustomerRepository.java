package com.example.ecommerceproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ecommerceproject.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{

}
