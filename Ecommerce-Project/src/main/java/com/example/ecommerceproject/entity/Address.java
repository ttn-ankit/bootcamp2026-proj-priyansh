package com.example.ecommerceproject.entity;

import com.example.ecommerceproject.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    String city;

    @Column(nullable = false, length = 20)
    String state;

    @Column(nullable = false, length = 20)
    String country;

    @Column(name = "address_line", nullable = false, length = 50)
    String addressLine;

    @Column(name = "zip_code", nullable = false, length = 20)
    String zipCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AddressType label;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false)
    AddressType addressType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
}
