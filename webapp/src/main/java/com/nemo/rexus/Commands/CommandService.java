package com.nemo.rexus.Commands;

import com.nemo.rexus.Decibel.CustomCommandRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.generated.tables.records.CustomCommandsRecord;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CommandService {

    private final CustomCommandRepository repository;

    @NotNull
    public CommandType parseCommandType(String commandTypeString, int robotId) {

        try {
            return StandardCommandType.valueOf(commandTypeString);
        } catch (IllegalArgumentException e) {
            CustomCommandType customCommandType = repository.getCustomCommandType(robotId, commandTypeString);

            if (customCommandType == null) throw new UnknownCommand(commandTypeString);

            return customCommandType;
        }
    }

    @NotNull
    public CommandType[] getAllCommandForRobot(int robotId) {
        return Stream.concat(
            Arrays.stream(StandardCommandType.values()),
            Arrays.stream(repository.getCommandsForRobot(robotId))
        ).toArray(CommandType[]::new);
    }

    public CustomCommandType insertOrUpdateCustomCommand(int robotId, String commandTypeString, String[] commandKeys) {
        return repository.insertOrUpdate(new CustomCommandsRecord(robotId, commandTypeString, commandKeys));
    }

    public boolean deleteCustomCommand(int robotId, String commandTypeString) {
        return repository.delete(robotId, commandTypeString);
    }

}
