package com.nemo.rexus.Sock.Messages;

import tools.jackson.core.JsonGenerator;

public record RegularMessage(String message) implements JsonMessage {

    @Override
    public MessageType getMessageType() {
        return MessageType.REGULAR_MESSAGE;
    }

    @Override
    public void addImplementationSpecificFields(JsonGenerator jsonGenerator) {
        jsonGenerator.writeStringProperty("message", message);
    }
}
