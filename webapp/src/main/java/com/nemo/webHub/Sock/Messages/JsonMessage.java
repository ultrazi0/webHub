package com.nemo.webHub.Sock.Messages;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.socket.TextMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface JsonMessage {

    MessageType getMessageType();

    void addImplementationSpecificFields(JsonGenerator jsonGenerator) throws IOException;

    default String jsonify() throws IOException {
        // See https://www.baeldung.com/jackson-streaming-api
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(stream, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(getMessageTypeFieldName(), getMessageType().toString());

        addImplementationSpecificFields(jsonGenerator);

        jsonGenerator.writeEndObject();

        jsonGenerator.close();

        return stream.toString(StandardCharsets.UTF_8);
    }

    @NotNull
    default TextMessage toTextMessage() throws IOException {
        return new TextMessage(jsonify());
    }

    @NotNull
    static String getMessageTypeFieldName() {
        return "messageType";
    }

    @NotNull
    static TextMessage createRegularJsonTextMessage(@NotNull CharSequence message) throws IOException {
        return new RegularMessage(message.toString()).toTextMessage();
    }

}
