package com.nemo.webHub.Robot;

import com.nemo.webHub.Commands.JsonCommand;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;



@Service
public class RobotService {

    private final HashMap<Integer, Robot> connectedRobotsHashMap = new HashMap<>();

    public void addConnectedRobot(Robot robot) {
        this.connectedRobotsHashMap.put(robot.getId(), robot);
        System.out.println("New robot connected, hashmap: " + this.connectedRobotsHashMap.toString());
    }

    public void removeConnectedRobot(int id) {
        this.connectedRobotsHashMap.remove(id);
        System.out.println("Robot disconnected, hashmap: " + this.connectedRobotsHashMap.toString());
    }

    public boolean robotIsConnected(int id) {
        return connectedRobotsHashMap.containsKey(id);
    }

    public Robot getRobotById(int id) {
        return this.connectedRobotsHashMap.get(id);
    }

    public void updateAndSendRobotState(int id, JsonCommand updateCommand) throws IOException {
        Robot robot = this.connectedRobotsHashMap.get(id);

        updateCommand.values().forEach((key, value) -> {
            // For each value in one command update this field (key - field name, value - field value)
            robot.setStateField(updateCommand.command(), key, value);
        });

        robot.sendRobotState(updateCommand.command());

    }

    public void sendStopToRobot(int id) throws IOException {
        connectedRobotsHashMap.get(id).sendStop();
    }

    public void sendRobotState(int id) throws IOException {
        Robot robot = this.connectedRobotsHashMap.get(id);

        robot.sendRobotState();

    }

}
