package com.nemo.webHub.Commands;

import lombok.Getter;

@Getter
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

}
