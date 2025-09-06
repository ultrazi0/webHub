package com.nemo.rexus.Sock.Command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.rexus.Robot.Robot;
import com.nemo.rexus.Robot.RobotConnectionService;
import com.nemo.rexus.Sock.Messages.JsonMessage;
import com.nemo.rexus.Sock.Messages.MessageType;
import com.nemo.rexus.Sock.OperatorController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

import static com.nemo.rexus.Sock.Messages.JsonMessage.createRegularJsonTextMessage;

/**
 *
 * Endpoint: /api/command/robot
 * <p>
 * This handler manages messages sent from the robot, in other words feedback.
 * Upon receiving such message, the handler redirects it to all the client-subscribers.
 * <p>
 * Message must be a parsable JSON, otherwise an exception is thrown.
 *
 */
@Slf4j
@RequiredArgsConstructor
public class CommandRobotHandler extends TextWebSocketHandler {

    private final RobotConnectionService robotConnectionService;
    private final OperatorController operatorController;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Object robotId = session.getAttributes().get("robotId");

        if (!(robotId instanceof Integer)) {
            throw new IllegalArgumentException(
                    "Expected an Integer for robotId, but received: " + robotId.getClass().getSimpleName()
            );
        }

        robotConnectionService.addConnectedRobot(new Robot((int) robotId, session));

        session.sendMessage(createRegularJsonTextMessage("Server>>> Connected to websocket at /api/command/robot"));

        String operatorSessionId = operatorController.getOperatorSessionId((int) robotId);

        if (operatorSessionId == null) {
            return;
        }

        WebSocketSession operatorSession = CommandClientHandler.getSession(operatorSessionId);
        operatorSession.sendMessage(createRegularJsonTextMessage("Robot with ID #" + robotId + " has just connected"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) throws RuntimeException, IOException {

        // Here "robotId" cannot be anything but an Integer,
        // because otherwise an exception would have been thrown in afterConnectionEstablished

        int robotId = (int) session.getAttributes().get("robotId");
        robotConnectionService.removeConnectedRobot(robotId);

        String operatorSessionId = operatorController.getOperatorSessionId(robotId);

        if (operatorSessionId == null) {
            return;
        }

        WebSocketSession operatorSession = CommandClientHandler.getSession(operatorSessionId);
        operatorSession.sendMessage(createRegularJsonTextMessage("Robot with ID #" + robotId + " has just disconnected"));

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, @NonNull TextMessage message) throws Exception {
        // Upon checking if a message is a JSON, redirects it to all subscribers

        int robotId = (int) session.getAttributes().get("robotId");
        String operatorSessionId = operatorController.getOperatorSessionId(robotId);

        if (operatorSessionId == null) {
            log.trace("Operator for robot with ID #{} is not connected yet", robotId);
            session.sendMessage(createRegularJsonTextMessage("Operator is not connected yet"));
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode messageNode = objectMapper.readTree(message.getPayload());
        JsonNode messageType = messageNode.get(JsonMessage.getMessageTypeFieldName());

        if (messageType == null) {
            throw new NoSuchFieldException("Message type not provided, revise your JSON");
        } else if (messageType.asText().equals(MessageType.FEEDBACK.toString())) {

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
