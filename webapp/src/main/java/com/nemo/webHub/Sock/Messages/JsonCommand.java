package com.nemo.webHub.Sock.Messages;

import com.fasterxml.jackson.core.*;
import com.nemo.webHub.Commands.CommandType;
import jakarta.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public record JsonCommand(CommandType command, Map<String, Double> values) implements JsonMessage {

    @Nullable
    public static JsonCommand createFromJson(String json) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser = jsonFactory.createParser(json);

        CommandType command = null;
        HashMap<String, Double> commandValues = new HashMap<>();

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
                command = CommandType.valueOf(jsonParser.getText());
            }

            if ("values".equals(fieldName)) {
                jsonParser.nextToken();
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    String valueName = jsonParser.currentName();
                    jsonParser.nextToken();
                    Double value = jsonParser.getValueAsDouble(0d);

                    commandValues.put(valueName, value);
                }
            }
        }

        jsonParser.close();

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
        jsonGenerator.writeStringField("command", String.valueOf(command));

        jsonGenerator.writeObjectFieldStart("values");
        for (Entry<String, Double> entry : values.entrySet()) {
            jsonGenerator.writeFieldName(entry.getKey().toLowerCase());
            jsonGenerator.writeNumber(entry.getValue());
        }
        jsonGenerator.writeEndObject();
    }

    public static String jsonifyMultipleCommands(List<JsonCommand> commandList) throws IOException {
        StringBuilder result = new StringBuilder("[");

        for (JsonCommand jsonCommand : commandList) {
            result.append(jsonCommand.jsonify());
            result.append(',');
        }

        result.append(']');

        return result.toString();

    }
}
