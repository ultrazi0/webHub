package com.nemo.rexus.Decibel;

import com.nemo.rexus.Commands.CommandType;
import com.nemo.rexus.Commands.CustomCommandType;
import com.nemo.rexus.Robot.RobotConnectionService;
import com.nemo.rexus.User.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record4;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RobotService {

    private final UserRepository userRepository;
    private final RobotRepository robotRepository;
    private final RobotConnectionService robotConnectionService;

    public @NotNull RobotEntity getRobotById(int id, int userId) {
        Record4<RobotsRecord, String, List<User>, List<CustomCommandType>> robotWithDetails = robotRepository.findRobotDetailedByIdIfAllowed(id, userId);
        return new RobotEntity(robotWithDetails.value1(), robotWithDetails.value2())
            .withSharedUsers(robotWithDetails.value3())
            .withCommands(robotWithDetails.value4())
            .withIsOnline(checkAvailability(robotWithDetails.value1().getRobotId()));
    }

    public @NotNull RobotEntity insertRobot(String name, int userId) {
        return new RobotEntity(
                robotRepository.insertNewRobot(name, userId),
                userRepository.findUserById(userId)
        );
    }

    @Transactional
    public @NotNull RobotEntity updateRobot(int robotId, String name, Collection<CommandType> commandTypes, int userId) {
        RobotEntity robot = new RobotEntity(
                robotRepository.updateRobot(robotId, name, userId),
                userRepository.findUserById(userId)
        );

        List<CustomCommandType> customCommandTypes = robotRepository.replaceCustomCommands(robotId, commandTypes)
            .map(CustomCommandType::of)
            .toList();

        return robot.withCommands(customCommandTypes);
    }

    public void deleteRobot(int id, int userId) {
        robotRepository.deleteRobot(id, userId);
    }

    public @NotNull Stream<RobotEntity> getUserRobots(int userId) {
        return robotRepository.getUserRobots(userId)
            .map(record -> new RobotEntity(record.value1(), record.value2())
                .withSharedUsers(record.value3())
                .withCommands(record.value4())
                .withIsOnline(checkAvailability(record.value1().getRobotId()))
            );
    }

    public boolean shareRobot(int robotId, int robotOwnerId, List<String> usernames) {
        return robotRepository.shareRobot(robotId, robotOwnerId, usernames);
    }

    public boolean unshareRobot(int robotId, int robotOwnerId, List<Integer> userIds) {
        return robotRepository.unshareRobot(robotId, robotOwnerId, userIds);
    }

    public @NotNull List<@NotNull User> getSharedUsers(int robotId, int robotOwnerId) {
        return robotRepository.getSharedUsers(robotId, robotOwnerId)
            .map(user -> new User(user.value1(), user.value2()))
            .toList();
    }

    private boolean checkAvailability(int robotId) {
        return robotConnectionService.robotIsConnected(robotId);
    }

}
