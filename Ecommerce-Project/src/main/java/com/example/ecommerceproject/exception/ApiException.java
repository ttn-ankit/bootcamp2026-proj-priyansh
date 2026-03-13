package com.example.ecommerceproject.exception;

public class ApiException extends RuntimeException{
    private final Integer status;

    public ApiException(String message, Integer status){
        super(message);
        this.status = status;
    }

    public Integer getStatus(){
        return status;
    }
}
