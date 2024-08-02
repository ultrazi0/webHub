package com.nemo.webHub.Decibel;

public class RobotNotFoundException extends RuntimeException {

    RobotNotFoundException(int id) {
        super("Could not find robot with ID #" + id);
    }

    RobotNotFoundException(String name) {
        super("Could not find robot with name \"" + name + "\"");
    }
}
