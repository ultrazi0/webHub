package com.nemo.testing.core.Formatters;

import com.nemo.webHub.Commands.CustomCommandType;
import com.tngtech.jgiven.format.ArgumentFormatter;

public class CustomCommandTypeFormatter implements ArgumentFormatter<CustomCommandType> {

    @Override
    public String format(CustomCommandType customCommandType, String... formatterArguments) {
        return formatCustomCommandType(customCommandType);
    }

    public static String formatCustomCommandType(CustomCommandType customCommandType) {
        return customCommandType.getCommandType() + "(" + String.join(", ", customCommandType.getKeys()) + ")";
    }

}
