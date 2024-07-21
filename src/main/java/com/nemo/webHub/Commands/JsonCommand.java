package com.nemo.webHub.Commands;

import com.fasterxml.jackson.core.*;
import com.nemo.webHub.Sock.Image.JsonImage;
import jakarta.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public record JsonCommand(CommandType command, Map<String, Double> values) {

    @Nullable
    public static JsonCommand createFromJson(String json) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser = jsonFactory.createParser(json);

        CommandType command = null;
        HashMap<String, Double> commandValues = new HashMap<>();

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();

            if ("messageType".equals(fieldName)) {
                jsonParser.nextToken();
                if (!jsonParser.getText().equals("command")) {
                    // If messageType says message is not a command, no need to parse further
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
                    String valueName = jsonParser.getCurrentName();
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

    public String jsonify() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(stream, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("messageType", "command");
        jsonGenerator.writeStringField("command", String.valueOf(command));

        jsonGenerator.writeObjectFieldStart("values");
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            jsonGenerator.writeFieldName(entry.getKey().toLowerCase());
            jsonGenerator.writeNumber(entry.getValue());
        }
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();

        jsonGenerator.close();

        return stream.toString(StandardCharsets.UTF_8);
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
