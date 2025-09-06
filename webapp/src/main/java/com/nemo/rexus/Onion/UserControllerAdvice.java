package com.nemo.rexus.Onion;

import com.nemo.rexus.Decibel.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<String> userNotFoundHandler(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("Content-Type", "application/json")
                .body("{\"error\": \"" + e.getMessage() + "\"}");
    }
}
