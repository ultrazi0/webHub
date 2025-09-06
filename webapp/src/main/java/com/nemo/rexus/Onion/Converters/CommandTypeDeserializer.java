package com.nemo.rexus.Onion.Converters;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nemo.rexus.Commands.CommandType;
import com.nemo.rexus.Commands.CustomCommandType;
import com.nemo.rexus.Commands.StandardCommandType;

import java.io.IOException;

public class CommandTypeDeserializer extends StdDeserializer<CommandType> {

    public CommandTypeDeserializer() {
        super(CommandType.class);
    }

    @Override
    public CommandType deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
            jsonParser.nextToken();
        }

        JsonNode node = context.readTree(jsonParser);
        if (!node.hasNonNull("commandType")) {
            throw new JsonParseException("No command type provided");
        }

        String command = node.get("commandType").asText();
        if (command.isBlank()) {
            throw new JsonParseException("Command type cannot be blank");
        }

        try {
            return StandardCommandType.valueOf(command);
        } catch (IllegalArgumentException e) {
            // not a standard command, proceed
        }

        ArrayNode keysNode;
        if (node.has("commandKeys")) {
            keysNode = node.withArrayProperty("commandKeys");
        } else if (node.has("keys")) {
            keysNode = node.withArrayProperty("keys");
        } else {
            keysNode = null;
        }
        String[] commandKeys;
        if (keysNode != null) {
            commandKeys = keysNode.valueStream().map(JsonNode::asText).filter(key -> !key.isBlank()).toArray(String[]::new);
        } else {
            commandKeys = new String[0];
        }

        return new CustomCommandType(command, commandKeys);
    }
}
