package com.nemo.webHub.Decibel;

import com.nemo.webHub.Commands.CustomCommandType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.generated.Keys;
import org.jooq.generated.tables.records.CustomCommandsRecord;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

import static org.jooq.generated.Tables.CUSTOM_COMMANDS;

@Repository
@RequiredArgsConstructor
public class CustomCommandRepository {

    private final DSLContext db;

    @Nullable
    public CustomCommandType getCustomCommandType(int robotId, String commandTypeString) {
        return db.selectFrom(CUSTOM_COMMANDS)
            .where(CUSTOM_COMMANDS.ROBOT_ID.eq(robotId))
            .and(CUSTOM_COMMANDS.COMMAND_TYPE.eq(commandTypeString))
            .fetchOptional()
            .map(CustomCommandType::of)
            .orElse(null);

    }

    @NotNull
    public CustomCommandType[] getCommandsForRobot(int robotId) {
        CustomCommandsRecord[] customCommandsRecords = db.selectFrom(CUSTOM_COMMANDS)
            .where(CUSTOM_COMMANDS.ROBOT_ID.equal(robotId))
            .fetchArray();

        return Arrays.stream(customCommandsRecords).map(CustomCommandType::of).toArray(CustomCommandType[]::new);
    }

    public CustomCommandType insertOrUpdate(CustomCommandsRecord customCommandsRecord) {
        CustomCommandsRecord insertedRecord = db.insertInto(CUSTOM_COMMANDS)
            .set(customCommandsRecord)
            .onDuplicateKeyUpdate()
            .set(customCommandsRecord)
            .returning()
            .fetchOne();

        return CustomCommandType.of(insertedRecord);
    }

    public boolean delete(int robotId, String commandTypeString) {
        return db.delete(CUSTOM_COMMANDS)
            .where(CUSTOM_COMMANDS.ROBOT_ID.eq(robotId).and(CUSTOM_COMMANDS.COMMAND_TYPE.eq(commandTypeString)))
            .execute() > 0;
    }

}
