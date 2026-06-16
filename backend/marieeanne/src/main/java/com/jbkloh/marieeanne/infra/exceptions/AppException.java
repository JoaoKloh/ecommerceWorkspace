package com.jbkloh.marieeanne.infra.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final HttpStatus httpStatus;

    public AppException(String message, HttpStatus httpStatus1){
        super(message);
        this.httpStatus = httpStatus1;
    }
    
}
