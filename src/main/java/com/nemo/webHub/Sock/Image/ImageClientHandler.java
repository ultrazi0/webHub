package com.nemo.webHub.Sock.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

import static com.nemo.webHub.Sock.WebSockConfig.createRegularJsonTextMessage;

public class ImageClientHandler extends TextWebSocketHandler {
    /*
    *
    * Endpoint: /api/image/client/{robotId}
    * This handler manages client image-websocket subscribers.
    * It is not the idea that it should handle messages.
    *
    * */


    @Autowired
    private ImageSubscribers imageSubscribers;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        int robotId = (int) session.getAttributes().get("robotId");

        imageSubscribers.removeSession(robotId, session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        System.out.println(">>> Something is going wrong <<<");

        System.out.println("**********************");

        imageSubscribers.printEverythingOut();

        System.out.println("**********************");

        session.sendMessage(createRegularJsonTextMessage("Please, don't do that"));

    }
}
