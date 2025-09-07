package com.nemo.rexus.Commands;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.jooq.generated.tables.records.CustomCommandsRecord;

import java.util.Arrays;

@ToString
@AllArgsConstructor
public class CustomCommandType implements CommandType {

    @Getter
    @NotEmpty
    private final String commandType;
    private final String[] commandKeys;

    @Override
    public String[] getKeys() {
        return commandKeys;
    }

    private CustomCommandType(CustomCommandsRecord customCommandsRecord) {
        this.commandType = customCommandsRecord.getCommandType();
        this.commandKeys = customCommandsRecord.getCommandKeys();
    }

    public static CustomCommandType of(CustomCommandsRecord customCommandsRecord) {
        return new CustomCommandType(customCommandsRecord);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        CustomCommandType that = (CustomCommandType) obj;
        return this.commandType.equals(that.commandType) && Arrays.equals(this.commandKeys, that.commandKeys);
    }
}
