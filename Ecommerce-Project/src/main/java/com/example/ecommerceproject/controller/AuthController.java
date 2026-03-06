package com.example.ecommerceproject.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceproject.dto.ApiResponseDTO;
import com.example.ecommerceproject.dto.ForgotPasswordRequestDTO;
import com.example.ecommerceproject.dto.LoginRequestDTO;
import com.example.ecommerceproject.dto.LoginResponseDTO;
import com.example.ecommerceproject.dto.RegisterRequestDTO;
import com.example.ecommerceproject.dto.ResetPasswordRequestDTO;
import com.example.ecommerceproject.dto.SellerRegisterRequestDTO;
import com.example.ecommerceproject.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;

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
    @PutMapping("/activate")
    public ResponseEntity<ApiResponseDTO> activateAccount(@RequestParam String token) {
        return ResponseEntity.ok(authService.activateAccount(token));
    }

    @Operation(summary = "Resend Activation Link")
    @PostMapping("/resend-activation")
    public ResponseEntity<ApiResponseDTO> resendActivation(@RequestBody String email) {
        return ResponseEntity.ok(authService.resendActivationLink(email));
    }

    @Operation(summary = "Regsiter new Seller")
    @PostMapping("/register/seller")
    public ResponseEntity<ApiResponseDTO> registerUserSeller(
            @Valid @RequestBody SellerRegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.ok(authService.registerSeller(registerRequestDTO));
    }

    @Operation(summary = "Login User")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Logout User")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        return ResponseEntity.ok(authService.logout(token));
    }

    @Operation(summary = "Request password reset link")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        return ResponseEntity.ok(authService.requestPasswordReset(dto));
    }

    @Operation(summary = "Reset password using token")
    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        return ResponseEntity.ok(authService.resetPassword(dto));
    }
}
