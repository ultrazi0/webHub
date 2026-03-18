package com.nemo.rexus.Onion.Converters;

import com.nemo.rexus.Commands.CommandType;
import com.nemo.rexus.Commands.CustomCommandType;
import com.nemo.rexus.Commands.StandardCommandType;
import org.springframework.boot.json.JsonParseException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.ArrayNode;

public class CommandTypeDeserializer extends StdDeserializer<CommandType> {

    public CommandTypeDeserializer() {
        super(CommandType.class);
    }

    @Override
    public CommandType deserialize(JsonParser jsonParser, DeserializationContext context) {
        if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
            jsonParser.nextToken();
        }

        JsonNode node = context.readTree(jsonParser);
        if (!node.hasNonNull("commandType")) {
            throw new JsonParseException(new IllegalArgumentException("No command type provided"));
        }

        String command = node.get("commandType").asString();
        if (command.isBlank()) {
            throw new JsonParseException(new IllegalArgumentException("Command type cannot be blank"));
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
            commandKeys = keysNode.valueStream().map(JsonNode::asString).filter(key -> !key.isBlank()).toArray(String[]::new);
        } else {
            commandKeys = new String[0];
        }

        return new CustomCommandType(command, commandKeys);
    }
}
