package com.noesis.pdf_extractor_tools.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtValidationException extends RuntimeException{
    public JwtValidationException(String message){
        super(message);
    }
}
