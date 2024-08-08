package com.nemo.webHub.Decibel;

public class UserNotFoundException extends RuntimeException {

    UserNotFoundException(int id) {
        super("Could not find user with ID #" + id);
    }

    UserNotFoundException(String username) {
        super("Could not find user \"" + username + "\"");
    }
}
