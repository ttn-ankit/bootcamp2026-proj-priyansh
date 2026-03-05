package com.example.ecommerceproject.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.LoginRequestDTO;
import com.example.ecommerceproject.dto.LoginResponseDTO;
import com.example.ecommerceproject.dto.RegisterRequestDTO;
import com.example.ecommerceproject.dto.SellerRegisterRequestDTO;
import com.example.ecommerceproject.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication APIs", description = "User registration and activation")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Regsiter new customer")
    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        return ResponseEntity.ok(authService.register(requestDTO));
    }

    @Operation(summary = "Activate user account using activation token")
    @GetMapping("/activate")
    public ResponseEntity<ApiResponseDTO> activateAccount(@RequestParam String token) {
        return ResponseEntity.ok(authService.activateAccount(token));
    }
    
    @Operation(summary = "Resend Activation Link")
    @GetMapping("/resend-activation")
    public ResponseEntity<ApiResponseDTO> resendActivation(@RequestParam String email){
        return ResponseEntity.ok(authService.resendActivationLink(email));
    }

    @Operation(summary = "Regsiter new Seller")
    @PostMapping("/register/seller")
    public ResponseEntity<ApiResponseDTO> registerUserSeller(@Valid @RequestBody SellerRegisterRequestDTO registerRequestDTO){
        return ResponseEntity.ok(authService.registerSeller(registerRequestDTO));
    }

    @Operation(summary = "Login User")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request){
        return ResponseEntity.ok(authService.login(request));
    }
}
