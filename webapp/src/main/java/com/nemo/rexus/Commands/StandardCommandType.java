package com.nemo.rexus.Commands;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StandardCommandType implements CommandType {
    MOVE(new String[] {"Speed", "Turn"}),
    TURRET(new String[] {"Tilt", "Turn"}),
    TURRET_CONTINUOUS(TURRET.getKeys()),
    AIM,
    SHOOT,
    STOP;

    private final String[] keys;

    @Override
    public String getCommandType() {
        return this.toString();
    }

    StandardCommandType(String[] keys) {
        this.keys = keys;
    }

    StandardCommandType() {
        this(new String[0]);
    }

}
