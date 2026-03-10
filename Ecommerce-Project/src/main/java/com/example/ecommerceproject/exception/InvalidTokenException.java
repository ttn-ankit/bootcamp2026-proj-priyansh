package com.example.ecommerceproject.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApiException{

    public InvalidTokenException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
