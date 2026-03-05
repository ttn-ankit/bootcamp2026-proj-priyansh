package com.example.ecommerceproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import static lombok.AccessLevel.PRIVATE;

import com.example.ecommerceproject.audit.Auditable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Seller extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "GST", nullable = false, unique = true, length = 15)
    String gst;

    @Column(name = "company_contact", nullable = false,length = 10)
    String companyContact;

    @Column(name = "company_name", nullable = false, length = 30)
    String companyName;

    @OneToOne(optional=false)
    @JoinColumn(name="user_id", unique=true)    
    User user;

    @Column(name = "is_approved")
    boolean isApproved;
}
