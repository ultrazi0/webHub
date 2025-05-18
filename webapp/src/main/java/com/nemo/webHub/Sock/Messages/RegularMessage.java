package com.nemo.webHub.Sock.Messages;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public record RegularMessage(String message) implements JsonMessage {

    @Override
    public MessageType getMessageType() {
        return MessageType.REGULAR_MESSAGE;
    }

    @Override
    public void addImplementationSpecificFields(JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStringField("message", message);
    }
}
