package com.nemo.webHub.Onion;

import com.nemo.webHub.Commands.CommandType;
import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.RobotRepository;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OnionAPIController {

    @Autowired
    private RobotRepository robotRepository;

    @GetMapping("/getAllCommands")
    public CommandType[] getAllCommands() {

        return CommandType.values();
    }

    @Nullable
    @PostMapping("/commandValues")
    public String[] commandValues(@Nullable @RequestParam("commandType") CommandType commandType) {
        if (commandType == null) {
            return null;
        }

        return commandType.getKeys();
    }

    @GetMapping("/getRobotById")
    public String getRobotById(@NotNull @RequestParam("id") int robotId) {
        RobotEntity robot = robotRepository.findRobotById(robotId);

        return robot != null ? robot.toString() : "No robot with ID #" + robotId;
    }

    @PostMapping("/insertNewRobot")
    public boolean insertNewRobot(@NotNull @RequestParam("name") String name) {
        return robotRepository.insertNewRobot(name);
    }

    @GetMapping("/getAllRobots")
    public RobotEntity[] getAllRobots() {
        return robotRepository.getAllRobots();
    }
}
