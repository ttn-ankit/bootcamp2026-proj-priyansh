package com.example.ecommerceproject.exception;

import org.springframework.http.HttpStatus;

public class AccountNotActiveException extends ApiException{

    public AccountNotActiveException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

}
