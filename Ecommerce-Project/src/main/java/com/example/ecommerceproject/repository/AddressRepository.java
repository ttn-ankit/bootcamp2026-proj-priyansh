package com.example.ecommerceproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerceproject.entity.Address;
import com.example.ecommerceproject.entity.User;
import com.example.ecommerceproject.enums.AddressTypeEnums;

public interface AddressRepository extends JpaRepository<Address, Long>{
    List<Address> findByUser(User user);
    
    Optional<Address> findByUserAndAddressTypeAndId(User user, AddressTypeEnums addressType, Long id);

    List<Address> findByUserAndAddressType(User user, AddressTypeEnums addressType);
}
