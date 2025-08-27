package com.nemo.testing.core.Persistence;

import com.nemo.testing.core.Persistence.UniqueAttributes.AbstractUniqueAttributes;
import com.nemo.testing.core.Persistence.UniqueAttributes.RobotUniqueAttributes;
import com.nemo.webHub.Commands.CustomCommandType;
import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.RobotNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep2;
import org.jooq.Record;
import org.jooq.generated.tables.records.CustomCommandsRecord;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.jooq.generated.Tables.CUSTOM_COMMANDS;
import static org.jooq.generated.Tables.ROBOTS;

/**
 * The {@code RobotService} class provides functionalities to interact with the robot records
 * in the database. It includes methods to find, create, and delete robot records, ensuring
 * encapsulation of database operations related to robots.
 */
@Slf4j
@Service
public class RobotService implements WithPersistence<RobotEntity> {

    @Autowired
    private DSLContext db;

    @Override
    public RobotEntity getEntityWith(AbstractUniqueAttributes uniqueAttributes) {
        if (uniqueAttributes instanceof RobotUniqueAttributes robotUniqueAttribute) {
            RobotsRecord robotsRecord = db
                .selectFrom(ROBOTS)
                .where(robotUniqueAttribute.buildWhereClause())
                .fetchOne();

            if (robotsRecord == null) throw new RobotNotFoundException(robotUniqueAttribute.toString());

            return RobotEntity.of(robotsRecord);
        }
        throw new IllegalStateException("uniqueAttributes is not an instance of RobotUniqueAttribute");
    }

    @Override
    public RobotEntity createEntityFrom(Record record) {
        if (record instanceof RobotsRecord) {
            RobotsRecord robotsRecord = db.insertInto(ROBOTS)
                .set(record)
                .returning()
                .fetchOne();

            if (robotsRecord == null) throw new RuntimeException("No robots were created");

            return RobotEntity.of(robotsRecord);
        }
        throw new IllegalStateException("record is not an instance of RobotsRecord");
    }


    @Override
    public RobotEntity getFrom(Record record) throws RuntimeException {
        if (record instanceof RobotsRecord robotsRecord) {
            if (robotsRecord.getName() != null && robotsRecord.getOwnerId() != null) {
                return findRobotByName(robotsRecord.getName(), robotsRecord.getOwnerId());
            }
            throw new IllegalStateException("not enough unique parameters");
        }
        throw new IllegalStateException("record is not an instance of RobotsRecord");
    }

    public RobotEntity findRobotByName(String robotName, int ownerId) throws RuntimeException {
        RobotsRecord robotsRecord = db
            .selectFrom(ROBOTS)
            .where(ROBOTS.NAME.eq(robotName).and(ROBOTS.OWNER_ID.eq(ownerId)))
            .fetchOne();

        if (robotsRecord == null) throw new RobotNotFoundException(robotName);

        return RobotEntity.of(robotsRecord);
    }

    public int getRobotOwnerIdByRobotId(int robotId) throws RobotNotFoundException {
        Integer ownerId = db
            .select(ROBOTS.OWNER_ID)
            .from(ROBOTS)
            .where(ROBOTS.ROBOT_ID.eq(robotId))
            .fetchOne(ROBOTS.OWNER_ID);

        if (ownerId == null) {
            throw new RobotNotFoundException(robotId);
        }

        return ownerId;
    }

    public List<CustomCommandType> getCustomRobotCommands(int robotId) throws RobotNotFoundException {
        Stream<CustomCommandsRecord> customCommandsRecordStream = db
            .selectFrom(CUSTOM_COMMANDS)
            .where(CUSTOM_COMMANDS.ROBOT_ID.eq(robotId))
            .fetchStream();

        return customCommandsRecordStream.map(CustomCommandType::of).toList();
    }

    public RobotEntity createNewRobot(String robotName, int ownerId) {
        RobotsRecord robotsRecord = db
            .insertInto(ROBOTS)
            .columns(ROBOTS.NAME, ROBOTS.OWNER_ID)
            .values(robotName, ownerId)
            .returning()
            .fetchOne();

        if (robotsRecord == null) {
            throw new RuntimeException("No robots were created");
        }

        return RobotEntity.of(robotsRecord);
    }

    public List<RobotEntity> createNewRobots(List<String> robotNames, int ownerId) {
        if (robotNames == null || robotNames.isEmpty()) {
            return Collections.emptyList();
        }

        InsertValuesStep2<RobotsRecord, String, Integer> insertQuery = db.insertInto(ROBOTS)
            .columns(ROBOTS.NAME, ROBOTS.OWNER_ID);

        for (String robotName : robotNames) {
            insertQuery = insertQuery.values(robotName, ownerId);
        }

        RobotsRecord[] robotsRecords = insertQuery.returning().fetchArray();

        return Arrays.stream(robotsRecords).map(RobotEntity::of).toList();
    }

    public void createCustomCommands(int robotId, Collection<CustomCommandType> customCommands) {
        if (customCommands == null || customCommands.isEmpty()) {
            return;
        }

        db.batchInsert(
            customCommands.stream()
                .map(customCommand ->
                    new CustomCommandsRecord(robotId, customCommand.getCommandType(), customCommand.getKeys()))
                .toList()
            ).execute();
    }

    public void deleteRobotById(int id) {
        int deleted = db.deleteFrom(ROBOTS).where(ROBOTS.ROBOT_ID.equal(id)).execute();

        if (deleted < 1) {
            throw new RuntimeException(id + " not found");
        }
    }

    public void deleteAllByIds(Collection<Integer> ids) {
        int deleted = db.
            deleteFrom(ROBOTS)
            .where(ROBOTS.ROBOT_ID.in(ids))
            .execute();

        if (deleted < 1) {
            log.warn("No robots were deleted, for there were none with id in {}", ids);
        }
    }

    @Override
    public void delete(Object entity) {
        if (entity instanceof RobotEntity robotEntity) {
            deleteRobotById(robotEntity.getId());
        }
        throw new IllegalArgumentException(
            "entity must be an instance of RobotEntity class, provided: " + entity);
    }

    @Override
    public void deleteAll(Collection<RobotEntity> entities) {
        deleteAllByIds(entities.stream().map(RobotEntity::getId).toList());
    }
}
