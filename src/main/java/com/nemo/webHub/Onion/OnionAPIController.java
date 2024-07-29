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
    public RobotEntity getRobotById(@NotNull @RequestParam("id") int robotId) {
        RobotEntity robot = robotRepository.findRobotById(robotId);

        if (robot == null) {
            throw new IllegalArgumentException("No robot with ID #" + robotId);
        }

        return robot;
    }

    @PostMapping("/insertNewRobot")
    public boolean insertNewRobot(@NotNull @RequestParam("name") String name) {
        return robotRepository.insertNewRobot(name);
    }

    @PostMapping("/updateRobot")
    public boolean updateRobot(@NotNull @RequestParam("id") int id, @NotNull @RequestParam("name") String name) {
        return robotRepository.updateRobot(id, name);
    }

    @DeleteMapping("/deleteRobot")
    public boolean deleteRobot(@NotNull @RequestParam("id") int id) {
        return robotRepository.deleteRobot(id);
    }

    @GetMapping("/getAllRobots")
    public RobotEntity[] getAllRobots() {
        return robotRepository.getAllRobots();
    }
}
