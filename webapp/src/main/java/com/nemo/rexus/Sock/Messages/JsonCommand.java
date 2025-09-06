package com.nemo.rexus.Sock.Messages;

import com.fasterxml.jackson.core.*;
import com.nemo.rexus.Commands.Command;
import com.nemo.rexus.Commands.CommandService;
import com.nemo.rexus.Commands.CommandType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record JsonCommand(CommandType command, Map<String, Double> values) implements Command, JsonMessage {

    @NotNull
    public static List<JsonCommand> createFromJson(String json, CommandService commandService, int robotId) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();

        List<JsonCommand> result = new ArrayList<>();
        try (JsonParser jsonParser = jsonFactory.createParser(json)) {
            switch (jsonParser.nextToken()) {
                case START_ARRAY -> {
                    do {
                        JsonCommand command = createFromJson(jsonParser, commandService, robotId);
                        if (command != null) {
                            result.add(command);
                        }
                    } while (jsonParser.nextToken() != JsonToken.END_ARRAY);
                }
                case START_OBJECT -> {
                    JsonCommand command = createFromJson(jsonParser, commandService, robotId);
                    if (command != null) {
                        result.add(command);
                    }
                }
                default -> throw new IOException("Unexpected token: " + jsonParser.currentToken());
            }
        }

        return result;
    }

    @Nullable
    private static JsonCommand createFromJson(JsonParser jsonParser, CommandService commandService, int robotId) throws IOException {
        CommandType command = null;
        Map<String, Double> commandValues = new HashMap<>();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.currentName();

            if (JsonMessage.getMessageTypeFieldName().equals(fieldName)) {
                jsonParser.nextToken();
                if (!MessageType.COMMAND.toString().equals(jsonParser.getText())) {
                    // If messageType says the message is not a command, no need to parse further
                    return null;
                }
            }

            if ("command".equals(fieldName)) {
                jsonParser.nextToken();
                command = commandService.parseCommandType(jsonParser.getText(), robotId);
            }

            if ("values".equals(fieldName)) {
                jsonParser.nextToken();
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    String valueName = jsonParser.currentName().toLowerCase();
                    jsonParser.nextToken();
                    Double value = jsonParser.getValueAsDouble(0d);

                    commandValues.put(valueName, value);
                }
            }
        }

        if (command == null) {
            return null;
        }

        return new JsonCommand(command, commandValues);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.COMMAND;
    }

    @Override
    public void addImplementationSpecificFields(JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStringField("command", command.getCommandType());

        jsonGenerator.writeObjectFieldStart("values");
        for (Entry<String, Double> entry : values.entrySet()) {
            jsonGenerator.writeFieldName(entry.getKey().toLowerCase());
            jsonGenerator.writeNumber(entry.getValue());
        }
        jsonGenerator.writeEndObject();
    }

    @Override
    public String[] getCommandProperties() {
        return command.getKeys();
    }

    @Override
    public boolean isValid() {
        for (String property : getCommandProperties()) {
            if (!values.containsKey(property.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}
