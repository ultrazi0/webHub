package com.nemo.rexus.Sock.Image;

import static com.nemo.rexus.Sock.Messages.JsonMessage.createRegularJsonTextMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * Endpoint: /api/image/client/{robotId}
 * <p>
 * This handler manages client image-websocket subscribers.
 * It is not the idea that it should handle messages.
 */
@Slf4j
@RequiredArgsConstructor
public class ImageClientHandler extends TextWebSocketHandler {

    private final ImageSubscribers imageSubscribers;

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        Object robotId = session.getAttributes().get("robotId");

        if (!(robotId instanceof Integer)) {
            // Should never happen because of the previous checks
            throw new IllegalArgumentException(
                    "Expected an Integer for robotId, but received: " + robotId.getClass().getSimpleName()
            );
        }

        imageSubscribers.addSession((int) robotId, session);

        // Greet the user
        session.sendMessage(createRegularJsonTextMessage("Server>>> Welcome to websocket at /api/image/client"));
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        int robotId = (int) session.getAttributes().get("robotId");

        imageSubscribers.removeSession(robotId, session);
    }

    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws IOException {

        log.warn("Image websocket is not supposed to receive messages, received: {} from {}", message, session);

        session.sendMessage(createRegularJsonTextMessage("Please, don't do that"));

    }
}
