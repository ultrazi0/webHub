package com.nemo.webHub.Sock.Command;

import com.nemo.webHub.Commands.CommandService;
import com.nemo.webHub.Sock.Messages.JsonCommand;
import com.nemo.webHub.Robot.RobotConnectionService;
import com.nemo.webHub.Sock.OperatorController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.nemo.webHub.Sock.Messages.JsonMessage.createRegularJsonTextMessage;


/**
 * Endpoint: /api/command/client/{robotId}
 * <p>
 * This handler manages messages sent from the client, thus primarily commands,
 * and redirects them to the robot.
 * <p>
 * Message must be a parsable JSON, otherwise an exception is thrown.
 */
@Slf4j
@RequiredArgsConstructor
public class CommandClientHandler extends TextWebSocketHandler {

    private final RobotConnectionService robotConnectionService;
    private final OperatorController operatorController;
    private final CommandService commandService;

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

        operatorController.addOperator(session.getId(), (int) robotId);
        sessionIdToSessionMap.put(session.getId(), session);

        // Greet the subscriber
        session.sendMessage(createRegularJsonTextMessage("Server>>> Connected to websocket at /api/command/client"));

        if (!robotConnectionService.robotIsConnected((int) robotId)) {
            return;
        }

        robotConnectionService.getRobotById((int) robotId).sendMessage(createRegularJsonTextMessage(
                "Operator has just been connected"
        ));
        robotConnectionService.sendStopToRobot((int) robotId);  // Ensures that the robot is not doing anything
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) throws RuntimeException, IOException {

        Integer robotId = operatorController.getRobotId(session.getId());

        operatorController.removeOperator(session.getId());
        sessionIdToSessionMap.remove(session.getId(), session);

        if (robotConnectionService.robotIsConnected(robotId)) {
            robotConnectionService.sendStopToRobot(robotId);
            robotConnectionService.getRobotById(robotId).sendMessage(createRegularJsonTextMessage(
                    "Operator has just disconnected"
            ));
        }

    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws IOException {
        log.trace("Transmitting message from client: {}", message.getPayload());

        int robotId = operatorController.getRobotId(session.getId());

        List<JsonCommand> commands = JsonCommand.createFromJson(message.getPayload(), commandService, robotId);

        if (commands.isEmpty()) {
            throw new IllegalArgumentException("Provided JSON has no commands");
        }

        log.trace("Created commands: {}", commands);

        if (!robotConnectionService.robotIsConnected(robotId)) {
            log.debug("Received a command, but robot with ID #{} has not connected yet", robotId);
            session.sendMessage(createRegularJsonTextMessage("Robot with this ID is not connected yet"));
            return;
        }

        robotConnectionService.handleCommands(robotId, commands, session);
    }

    static WebSocketSession getSession(String sessionId) {
        return sessionIdToSessionMap.get(sessionId);
    }
}
