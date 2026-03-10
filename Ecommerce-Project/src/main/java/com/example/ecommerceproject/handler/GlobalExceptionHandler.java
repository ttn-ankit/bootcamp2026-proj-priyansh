package com.example.ecommerceproject.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
<<<<<<< HEAD
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
=======
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
>>>>>>> bdb0356 (Refactored)
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
<<<<<<< HEAD
import com.example.ecommerceproject.dto.ValidationErrorResponseDTO;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.util.MessageService;
import com.example.ecommerceproject.constants.MessageKeys;
=======

import com.example.ecommerceproject.dto.ValidationErrorResponseDTO;
import com.example.ecommerceproject.util.MessageKeys;
import com.example.ecommerceproject.exception.ApiException;
>>>>>>> bdb0356 (Refactored)

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

<<<<<<< HEAD
    private final MessageService messageService;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException apiException) {
        String message = messageService.getMessage(apiException.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", apiException.getStatus());
        return new ResponseEntity<>(response, HttpStatus.valueOf(apiException.getStatus()));
=======
    private final MessageSource messageSource;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException apiException) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(apiException.getMessage(), null, apiException.getMessage(), locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", apiException.getStatus().value());
        return new ResponseEntity<>(response, apiException.getStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(MessageKeys.AUTH_INVALID_CREDENTIALS, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
>>>>>>> bdb0356 (Refactored)
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLocked(LockedException ex) {
<<<<<<< HEAD
        String message = messageService.getMessage(MessageKeys.AUTH_ACCOUNT_LOCKED);
=======
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(MessageKeys.AUTH_ACCOUNT_LOCKED, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabled(DisabledException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(MessageKeys.AUTH_ACCOUNT_NOT_ACTIVATED, null, locale);
>>>>>>> bdb0356 (Refactored)
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleCredentialsExpired(CredentialsExpiredException ex) {
<<<<<<< HEAD
        String message = messageService.getMessage(MessageKeys.AUTH_PASSWORD_EXPIRED);
=======
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(MessageKeys.AUTH_PASSWORD_EXPIRED, null, locale);
>>>>>>> bdb0356 (Refactored)
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
<<<<<<< HEAD
        String message = messageService.getMessage(MessageKeys.VALIDATION_FAILED);
=======
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(MessageKeys.VALIDATION_FAILED, null, locale);
>>>>>>> bdb0356 (Refactored)
        List<ValidationErrorResponseDTO.FieldErrorDTO> errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> new ValidationErrorResponseDTO.FieldErrorDTO(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());
        ValidationErrorResponseDTO response = new ValidationErrorResponseDTO(
                LocalDateTime.now(),
                message,
                errors,
                HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
<<<<<<< HEAD
        String message = messageService.getMessage(MessageKeys.ERROR_INTERNAL_SERVER);
=======
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(MessageKeys.ERROR_INTERNAL_SERVER, null, locale);
>>>>>>> bdb0356 (Refactored)
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", 500);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        Locale locale = LocaleContextHolder.getLocale();
<<<<<<< HEAD
        String message = messageSource.getMessage("validation.failed", null, locale);
=======
        String message = messageSource.getMessage(MessageKeys.VALIDATION_FAILED, null, locale);
>>>>>>> bdb0356 (Refactored)
        List<ValidationErrorResponseDTO.FieldErrorDTO> errors = ex.getConstraintViolations()
            .stream()
            .map(violation -> {
                String fieldPath = violation.getPropertyPath().toString();
                return new ValidationErrorResponseDTO.FieldErrorDTO(fieldPath, violation.getMessage());
            })
            .collect(Collectors.toList());
            
        ValidationErrorResponseDTO response = new ValidationErrorResponseDTO(
                LocalDateTime.now(),
                message,
                errors,
                HttpStatus.BAD_REQUEST.value());
                
        return ResponseEntity.badRequest().body(response);
    }
}
