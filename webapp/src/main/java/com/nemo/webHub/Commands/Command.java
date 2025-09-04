package com.nemo.webHub.Commands;

public interface Command {

    String[] getCommandProperties();

    boolean isValid();

}
