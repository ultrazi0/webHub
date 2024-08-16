package com.nemo.webHub.Decibel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RobotNotFoundException extends RuntimeException {

    RobotNotFoundException(int id) {
        super("Could not find robot with ID #" + id);
    }

    RobotNotFoundException(String name) {
        super("Could not find robot with name \"" + name + "\"");
    }
}
