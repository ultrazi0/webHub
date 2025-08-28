package com.nemo.webHub.Robot;

import com.nemo.webHub.Commands.Aim.AimLogic;
import com.nemo.webHub.Commands.CustomCommandType;
import com.nemo.webHub.Commands.StandardCommandType;
import com.nemo.webHub.Sock.Image.ImageSubscribers;
import com.nemo.webHub.Sock.Messages.JsonCommand;
import com.nemo.webHub.Config;
import com.nemo.webHub.Sock.Image.JsonImage;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

import static com.nemo.webHub.Sock.Messages.JsonMessage.createRegularJsonTextMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class RobotConnectionService {

    private final Config config;
    private final ImageSubscribers imageSubscribers;

    private final Map<Integer, Robot> connectedRobotsMap = new HashMap<>();

    public void addConnectedRobot(Robot robot) {
        connectedRobotsMap.put(robot.getId(), robot);
        log.trace("New robot connected, hashmap: {}", connectedRobotsMap);
    }

    public void removeConnectedRobot(int id) {
        connectedRobotsMap.remove(id);
        JsonImage.removeFromLastImageMap(id);

        log.trace("Robot disconnected, hashmap: {}", connectedRobotsMap);
    }

    public boolean robotIsConnected(int id) {
        return connectedRobotsMap.containsKey(id);
    }

    public Robot getRobotById(int id) {
        return connectedRobotsMap.get(id);
    }

    public void handleCommands(int robotId, List<JsonCommand> commands, WebSocketSession clientSession) throws IOException {
        Robot robot = connectedRobotsMap.get(robotId);

        List<JsonCommand> commandsToSend = new LinkedList<>();
        for (JsonCommand command : commands) {
            if (command.isValid()) {
                JsonCommand commandToSend = null;
                if (command.command() instanceof StandardCommandType standardCommand) {
                    commandToSend = switch (standardCommand) {
                        case MOVE, TURRET, TURRET_CONTINUOUS, STOP, SHOOT -> command;
                        case AIM -> {
                            JsonImage lastImage = JsonImage.getLastImage(robotId);

                            if (lastImage == null) {
                                clientSession.sendMessage(createRegularJsonTextMessage("Uhm... no image, check the connection"));
                                yield null;
                            }

                            JsonCommand aimCommand = createAimCommand(lastImage);

                            imageSubscribers.sendMessageToAllSessions(robotId, lastImage.asAimImage().toTextMessage());

                            if (aimCommand == null) {
                                clientSession.sendMessage(createRegularJsonTextMessage("No QR-code found, better luck next time!"));
                                yield null;
                            }

                            clientSession.sendMessage(createRegularJsonTextMessage("Fire 'er up, sir!"));
                            yield aimCommand;
                        }
                    };
                } else if (command.command() instanceof CustomCommandType) {
                   commandToSend = command;
                }

                if (commandToSend != null) {
                    commandsToSend.add(commandToSend);
                }
            }
        }

        robot.sendCommands(commandsToSend);
    }

    public void sendStopToRobot(int id) throws IOException {
        connectedRobotsMap.get(id).sendStop();
    }

    @Nullable
    private JsonCommand createAimCommand(@NotNull JsonImage lastImage) {
        double[] angles = AimLogic.aim(lastImage, config);

        return angles == null ? null : AimLogic.createCommand(angles);
    }

}
