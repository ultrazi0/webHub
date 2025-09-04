package com.nemo.testing.core.Formatters;

import com.nemo.webHub.Commands.CustomCommandType;
import com.tngtech.jgiven.format.ArgumentFormatter;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomCommandTypeArrayFormatter implements ArgumentFormatter<CustomCommandType[]> {

    @Override
    public String format(CustomCommandType[] argumentToFormat, String... formatterArguments) {
        return "["
            + Arrays.stream(argumentToFormat)
            .map(CustomCommandTypeFormatter::formatCustomCommandType)
            .collect(Collectors.joining(", "))
            + "]";
    }
}
