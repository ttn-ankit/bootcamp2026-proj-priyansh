package com.example.ecommerceproject.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.ecommerceproject.dto.ValidationErrorResponseDTO;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.util.MessageKeys;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException apiException) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(apiException.getMessage(), null, apiException.getMessage(), locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleDisabledException(DisabledException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.AUTH_ACCOUNT_NOT_ACTIVATED, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<?> handleLockedException(LockedException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.AUTH_ACCOUNT_LOCKED, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<?> handleCredentialsExpiredException(CredentialsExpiredException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.AUTH_PASSWORD_EXPIRED, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get(MessageKeys.AUTH_INVALID_CREDENTIALS, null, locale);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        String message = messageService.get(MessageKeys.VALIDATION_FAILED);
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
        String message = messageService.get(MessageKeys.ERROR_INTERNAL_SERVER);
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", 500);
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageService.get("validation.failed", null, locale);
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