package com.nemo.rexus.Sock.Messages;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.socket.TextMessage;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.json.JsonFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface JsonMessage {

    MessageType getMessageType();

    void addImplementationSpecificFields(JsonGenerator jsonGenerator);

    @NotNull
    default TextMessage toTextMessage() throws IOException {
        // See https://www.baeldung.com/jackson-streaming-api
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonFactory jsonFactory = new JsonFactory();

        try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(ObjectWriteContext.empty(), stream, JsonEncoding.UTF8)) {
            writeMessage(jsonGenerator);
        }

        return new TextMessage(stream.toString(StandardCharsets.UTF_8));
    }

    private void writeMessage(JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringProperty(getMessageTypeFieldName(), getMessageType().toString());

        addImplementationSpecificFields(jsonGenerator);

        jsonGenerator.writeEndObject();
    }

    @NotNull
    static String getMessageTypeFieldName() {
        return "messageType";
    }

    @NotNull
    static TextMessage createRegularJsonTextMessage(@NotNull CharSequence message) throws IOException {
        return new RegularMessage(message.toString()).toTextMessage();
    }

    @NotNull
    static TextMessage createMultipleMessages(List<? extends JsonMessage> messages) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonFactory jsonFactory = new JsonFactory();

        try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(ObjectWriteContext.empty(), stream, JsonEncoding.UTF8)) {
            jsonGenerator.writeStartArray();

            for (JsonMessage jsonMessage : messages) {
                jsonMessage.writeMessage(jsonGenerator);
            }

            jsonGenerator.writeEndArray();
        }

        return new TextMessage(stream.toString(StandardCharsets.UTF_8));
    }

}
