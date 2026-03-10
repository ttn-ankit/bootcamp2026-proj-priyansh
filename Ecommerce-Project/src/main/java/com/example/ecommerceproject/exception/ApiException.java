package com.example.ecommerceproject.exception;

<<<<<<< HEAD
public class ApiException extends RuntimeException{
    private final Integer HttpStatus;

    public ApiException(String message, Integer status){
        super(message);
        this.HttpStatus = status;
    }

    public Integer getStatus(){
        return HttpStatus;
=======
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException{
    private final HttpStatus status;

    public ApiException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus(){
        return status;
>>>>>>> bdb0356 (Refactored)
    }
}
