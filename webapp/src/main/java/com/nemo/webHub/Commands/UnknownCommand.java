package com.nemo.webHub.Commands;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownCommand extends RuntimeException {

    public UnknownCommand(String commandTypeString) {
        super("Unknown command: " + commandTypeString);
    }

}
