package com.nemo.webHub.Sock.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

import static com.nemo.webHub.Sock.WebSockConfig.createRegularJsonTextMessage;

public class ImageRobotHandler extends TextWebSocketHandler {
    /*
     *
     * Endpoint: /api/image/robot/{robotId}
     * This handler manages images sent from the robot.
     * Upon saving the last image, it retransmits it to the client.
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

        imageSubscribers.addRobot((int) robotId);

        // Greet the robot
        session.sendMessage(createRegularJsonTextMessage("Server>>> Welcome to websocket at /api/image/robot"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        int robotId = (int) session.getAttributes().get("robotId");

        imageSubscribers.removeRobot(robotId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        try {

            JsonImage image = JsonImage.createFromJson(message.getPayload());

            if (image != null) {
                JsonImage.setLastImage(image);
            } else {
                message = createRegularJsonTextMessage(
                        "Server>>> Provided JSON has no image field and/or is not messageType \"image\""
                );
            }
            System.out.println("Server>>> Got image: " + message);

            int robotId = (int) session.getAttributes().get("robotId");

            for (WebSocketSession sessionListener : imageSubscribers.getSessionsByRobotId(robotId)) {
                sessionListener.sendMessage(message);
            }
        } catch (IOException e) {
            System.out.println("!>> Supplied file is incorrect");
            throw e;
        }
    }
}
