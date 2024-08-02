package com.nemo.webHub.Onion;

import com.nemo.webHub.Decibel.RobotNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RobotControllerAdvice {

    @ExceptionHandler(RobotNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String robotNotFoundHandler(RobotNotFoundException e) {
        return "{error: \"" + e.getMessage() + "\"}";
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String robotNameAlreadyTaken(DataAccessException e) {
        return "{error: \"Name already taken\"}";
    }
}
