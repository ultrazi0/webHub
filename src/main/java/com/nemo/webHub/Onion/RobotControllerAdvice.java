package com.nemo.webHub.Onion;

import com.nemo.webHub.Decibel.RobotNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RobotControllerAdvice {

    @ExceptionHandler(RobotNotFoundException.class)
    ResponseEntity<String> robotNotFoundHandler(RobotNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("Content-Type", "application/json")
                .body("{\"error\": \"" + e.getMessage() + "\"}");
    }

    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<String> robotNameAlreadyTaken(DataAccessException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .header("Content-Type", "application/json")
                .body("{\"error\": \"Name already taken\"}");
    }
}
