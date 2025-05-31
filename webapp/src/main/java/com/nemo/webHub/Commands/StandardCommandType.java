package com.nemo.webHub.Commands;

import lombok.Getter;

@Getter
public enum StandardCommandType implements CommandType {
    MOVE(new String[] {"Speed", "Turn"}),
    TURRET(new String[] {"Tilt", "Turn"}),
    AIM,
    SHOOT,
    STOP;

    private final String[] keys;

    StandardCommandType(String[] keys) {
        this.keys = keys;
    }

    StandardCommandType() {
        this(new String[0]);
    }

}
