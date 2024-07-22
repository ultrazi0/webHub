package com.nemo.webHub.Sock.Command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.webHub.Commands.Aim.AimLogic;
import com.nemo.webHub.Commands.JsonCommand;
import com.nemo.webHub.Config;
import com.nemo.webHub.Robot.RobotService;
import com.nemo.webHub.Sock.Image.JsonImage;
import com.nemo.webHub.Sock.Operators;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;

import static com.nemo.webHub.Sock.WebSockConfig.createRegularJsonTextMessage;


public class CommandClientHandler extends TextWebSocketHandler {
    /*
    *
    * Endpoint: /api/command/client/{robotId}
    * This handler manages messages sent from the client, thus primarily commands,
    * and redirects them to the robot.
    *
    * Message must be a parsable JSON, otherwise an exception is thrown.
    *
    * */

    @Autowired
    private CommandSubscribers commandSubscribers;
    @Autowired
    private RobotService robotService;
    @Autowired
    private Operators operators;
    @Autowired
    private Config config;

    private static final HashMap<String, WebSocketSession> sessionIdToSessionMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Object robotId = session.getAttributes().get("robotId");

        if (!(robotId instanceof Integer)) {
            // Should never happen because of the previous checks
            throw new IllegalArgumentException(
                    "Expected an Integer for robotId, but received: " + robotId.getClass().getSimpleName()
            );
        }

        operators.addOperator(session.getId(), (int) robotId);
        sessionIdToSessionMap.put(session.getId(), session);

        // Greet the subscriber
        session.sendMessage(createRegularJsonTextMessage("Server>>> Connected to websocket at /api/command/client"));

        if (!robotService.robotIsConnected((int) robotId)) {
            return;
        }

        robotService.getRobotById((int) robotId).sendMessage(createRegularJsonTextMessage(
                "Operator has just been connected"
        ));
        robotService.sendStopToRobot((int) robotId);  // Ensures that the robot is not doing anything
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws RuntimeException, IOException {

        Integer robotId = operators.getRobotId(session.getId());

        operators.removeOperator(session.getId());
        sessionIdToSessionMap.remove(session.getId(), session);

        if (robotService.robotIsConnected(robotId)) {
            robotService.sendStopToRobot(robotId);
            robotService.getRobotById(robotId).sendMessage(createRegularJsonTextMessage(
                    "Operator has just disconnected"
            ));
        }

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Transmitting message from client: " + message.getPayload());

        JsonCommand command = JsonCommand.createFromJson(message.getPayload());

        if (command == null) {
            throw new NoSuchFieldException("Provided JSON has no field \"command\"");
        }

        System.out.println("Created command: " + command);

        int robotId = operators.getRobotId(session.getId());

        if (!robotService.robotIsConnected(robotId)) {
            System.out.println("Received a command, but robot with ID #" + robotId + " has not connected yet");
            session.sendMessage(createRegularJsonTextMessage("Robot with this ID is not connected yet"));
            return;
        }

        switch (command.command()) {
            case MOVE, TURRET -> robotService.updateAndSendRobotState(robotId, command);
            case STOP -> robotService.sendStopToRobot(robotId);
            case AIM -> {
                Boolean success = robotService.startAimAndSendResult(robotId);

                if (Boolean.TRUE.equals(success)) {
                    session.sendMessage(createRegularJsonTextMessage("Fire 'er up, sir!"));
                } else if (Boolean.FALSE.equals(success)) {
                    session.sendMessage(createRegularJsonTextMessage("No QR-code found, better luck next time!"));
                } else {
                    session.sendMessage(createRegularJsonTextMessage("Uhm... no image, check the connection"));
                }
            }
            case SHOOT -> session.sendMessage(createRegularJsonTextMessage(
                    "I hear you, but you have to use use your imagination for now :("
            ));
        }

    }

    static WebSocketSession getSession(String sessionId) {
        return sessionIdToSessionMap.get(sessionId);
    }

    @Deprecated
    protected void handleTextMessageDeprecated(WebSocketSession session, TextMessage message) throws Exception {

        System.out.println("Transmitting message from client: " + message.getPayload());

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode messageNode = objectMapper.readTree(message.getPayload());
        JsonNode messageType = messageNode.get("messageType");

        if (messageType == null) {
            throw new NoSuchFieldException("Message type not provided, revise your JSON");
        } else if (messageType.asText().equals("command")) {

            if (!messageNode.has("command")) {
                throw new NoSuchFieldException("Message is categorised as a command, and yet has no \"command\" field");
            }

            if (messageNode.get("command").asText().equals("AIM")) {

                JsonImage lastImage = JsonImage.getLastImage();

                if (lastImage != null) {

                    double[] angles = AimLogic.aim(lastImage, config);

                    if (angles != null) {
                        JsonCommand aimCommand = AimLogic.createCommand(angles);

                        message = new TextMessage(aimCommand.jsonify());
                    } else {
                        session.sendMessage(createRegularJsonTextMessage("No QR-code detected"));
                    }
                }
            }

            for (WebSocketSession value : commandSubscribers.getSubscribers().values()) {
                value.sendMessage(message);
            }
        } else {
            session.sendMessage(createRegularJsonTextMessage("Unknown or unsupported message type"));
        }

    }
}
