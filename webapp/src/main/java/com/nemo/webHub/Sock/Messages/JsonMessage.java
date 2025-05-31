package com.nemo.webHub.Sock.Messages;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.socket.TextMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface JsonMessage {

    MessageType getMessageType();

    void addImplementationSpecificFields(JsonGenerator jsonGenerator) throws IOException;

    @NotNull
    default TextMessage toTextMessage() throws IOException {
        // See https://www.baeldung.com/jackson-streaming-api
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonFactory jsonFactory = new JsonFactory();

        try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(stream, JsonEncoding.UTF8)) {
            writeMessage(jsonGenerator);
        }

        return new TextMessage(stream.toString(StandardCharsets.UTF_8));
    }

    private void writeMessage(JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(getMessageTypeFieldName(), getMessageType().toString());

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

        try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(stream, JsonEncoding.UTF8)) {
            jsonGenerator.writeStartArray();

            for (JsonMessage jsonMessage : messages) {
                jsonMessage.writeMessage(jsonGenerator);
            }

            jsonGenerator.writeEndArray();
        }

        return new TextMessage(stream.toString(StandardCharsets.UTF_8));
    }

}
