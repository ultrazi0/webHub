package com.nemo.webHub.Robot;

import com.nemo.webHub.Commands.Aim.AimLogic;
import com.nemo.webHub.Commands.JsonCommand;
import com.nemo.webHub.Config;
import com.nemo.webHub.Sock.Image.JsonImage;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.HashMap;



@Service
public class RobotService {

    @Autowired
    private Config config;

    private final HashMap<Integer, Robot> connectedRobotsHashMap = new HashMap<>();

    public void addConnectedRobot(Robot robot) {
        connectedRobotsHashMap.put(robot.getId(), robot);
        System.out.println("New robot connected, hashmap: " + connectedRobotsHashMap.toString());
    }

    public void removeConnectedRobot(int id) {
        connectedRobotsHashMap.remove(id);
        System.out.println("Robot disconnected, hashmap: " + connectedRobotsHashMap.toString());
    }

    public boolean robotIsConnected(int id) {
        return connectedRobotsHashMap.containsKey(id);
    }

    public Robot getRobotById(int id) {
        return connectedRobotsHashMap.get(id);
    }

    public void updateAndSendRobotState(int id, JsonCommand updateCommand) throws IOException {
        Robot robot = connectedRobotsHashMap.get(id);

        updateCommand.values().forEach((key, value) -> {
            // For each value in one command update this field (key - field name, value - field value)
            robot.setStateField(updateCommand.command(), key, value);
        });

        robot.sendRobotState(updateCommand.command());

    }

    public void sendStopToRobot(int id) throws IOException {
        connectedRobotsHashMap.get(id).sendStop();
    }

    @Nullable
    public Boolean startAimAndSendResult(int id) throws IOException {
        if (JsonImage.lastImage != null) {
            double[] angles = AimLogic.aim(JsonImage.lastImage,config);

            if (angles != null) {
                JsonCommand aimCommand = AimLogic.createCommand(angles);

                Robot robot = connectedRobotsHashMap.get(id);
                robot.sendMessage(new TextMessage(aimCommand.jsonify()));

                return true;
            } else {
                return false;
            }
        }
        return null;
    }

    public void sendRobotState(int id) throws IOException {
        Robot robot = connectedRobotsHashMap.get(id);

        robot.sendRobotState();

    }

}
