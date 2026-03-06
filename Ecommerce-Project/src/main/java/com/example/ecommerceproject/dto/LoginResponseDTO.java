package com.example.ecommerceproject.dto;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private List<? extends GrantedAuthority> role;
    private String email;
    private String message;
}