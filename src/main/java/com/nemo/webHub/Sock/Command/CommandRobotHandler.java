package com.nemo.webHub.Sock.Command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.webHub.Robot.Robot;
import com.nemo.webHub.Robot.RobotService;
import com.nemo.webHub.Sock.Operators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

import static com.nemo.webHub.Sock.WebSockConfig.createRegularJsonTextMessage;

public class CommandRobotHandler extends TextWebSocketHandler {
    /*
    *
    * Endpoint: /api/command/robot/{robotId}
    * This handler manages messages sent from the robot, in other words feedback.
    * Upon receiving such message, the handler redirects it to all the client-subscribers.
    *
    * Message must be a parsable JSON, otherwise an exception is thrown.
    *
    * */


    @Autowired
    private RobotService robotService;
    @Autowired
    private Operators operators;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Object robotId = session.getAttributes().get("robotId");

        if (!(robotId instanceof Integer)) {
            throw new IllegalArgumentException(
                    "Expected an Integer for robotId, but received: " + robotId.getClass().getSimpleName()
            );
        }

        robotService.addConnectedRobot(new Robot((int) robotId, session));

        session.sendMessage(createRegularJsonTextMessage("Server>>> Connected to websocket at /api/command/robot"));

        String operatorSessionId = operators.getOperatorSessionId((int) robotId);

        if (operatorSessionId == null) {
            return;
        }

        WebSocketSession operatorSession = CommandClientHandler.getSession(operatorSessionId);
        operatorSession.sendMessage(createRegularJsonTextMessage("Robot with ID #" + robotId + " has just connected"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws RuntimeException, IOException {

        // Here "robotId" cannot be anything but an Integer,
        // because otherwise an exception would have been thrown in afterConnectionEstablished

        int robotId = (int) session.getAttributes().get("robotId");
        robotService.removeConnectedRobot(robotId);

        String operatorSessionId = operators.getOperatorSessionId(robotId);

        if (operatorSessionId == null) {
            return;
        }

        WebSocketSession operatorSession = CommandClientHandler.getSession(operatorSessionId);
        operatorSession.sendMessage(createRegularJsonTextMessage("Robot with ID #" + robotId + " has just disconnected"));

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Upon checking if message is a JSON, redirects it to all subscribers

        int robotId = (int) session.getAttributes().get("robotId");
        String operatorSessionId = operators.getOperatorSessionId(robotId);

        if (operatorSessionId == null) {
            System.out.println("Operator for robot with ID #" + robotId + " is not connected yet");
            session.sendMessage(createRegularJsonTextMessage("Operator is not connected yet"));
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode messageNode = objectMapper.readTree(message.getPayload());
        JsonNode messageType = messageNode.get("messageType");

        if (messageType == null) {
            throw new NoSuchFieldException("Message type not provided, revise your JSON");
        } else if (messageType.asText().equals("feedback")) {

            if (!messageNode.has("feedback")) {
                throw new NoSuchFieldException(
                        "Message is categorised as a feedback, and yet has no \"feedback\" field"
                );
            }

            WebSocketSession operatorSession = CommandClientHandler.getSession(operatorSessionId);
            operatorSession.sendMessage(message);

        } else {
            session.sendMessage(createRegularJsonTextMessage("Unknown or unsupported message type"));
        }

    }
}
