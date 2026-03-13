package com.example.ecommerceproject.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.ecommerceproject.dto.ValidationErrorResponseDTO;
import com.example.ecommerceproject.exception.ApiException;
import com.example.ecommerceproject.util.MessageService;
import com.example.ecommerceproject.constants.MessageKeys;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException apiException) {
        String message = messageService.getMessage(apiException.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", apiException.getStatus());
        return new ResponseEntity<>(response, HttpStatus.valueOf(apiException.getStatus()));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLocked(LockedException ex) {
        String message = messageService.getMessage(MessageKeys.AUTH_ACCOUNT_LOCKED);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleCredentialsExpired(CredentialsExpiredException ex) {
        String message = messageService.getMessage(MessageKeys.AUTH_PASSWORD_EXPIRED);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        String message = messageService.getMessage(MessageKeys.VALIDATION_FAILED);
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
        String message = messageService.getMessage(MessageKeys.ERROR_INTERNAL_SERVER);
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", 500);
        return ResponseEntity.internalServerError().body(response);
    }
}
