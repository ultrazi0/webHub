package com.nemo.rexus.Commands;

public interface Command {

    String[] getCommandProperties();

    boolean isValid();

}
