package com.nemo.webHub.Commands;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jooq.generated.tables.records.CustomCommandsRecord;

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

}
