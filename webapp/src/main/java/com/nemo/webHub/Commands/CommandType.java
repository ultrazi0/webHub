package com.nemo.webHub.Commands;

public enum CommandType {
    MOVE (new String[] {"Speed", "Turn"}),
    TURRET (new String[] {"Tilt", "Turn"}),
    AIM (new String[] {}),
    SHOOT (new String[] {}),
    STOP (new String[] {});

    private final String[] keys;

    CommandType(String[] keys) {
        this.keys = keys;
    }

    public String[] getKeys() {
        return keys;
    }
}
