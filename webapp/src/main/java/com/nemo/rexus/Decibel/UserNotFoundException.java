package com.nemo.rexus.Decibel;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {

    UserNotFoundException(int id) {
        super("Could not find user with ID #" + id);
    }

    public UserNotFoundException(String username) {
        super("Could not find user \"" + username + "\"");
    }
}
