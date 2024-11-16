package com.nemo.testing.core.Persistence;

import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.RobotNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static org.jooq.generated.Tables.ROBOTS;
import static org.jooq.generated.Tables.USER_ROBOT_RELATIONS;

/**
 * The {@code RobotService} class provides functionalities to interact with the robot records
 * in the database. It includes methods to find, create, and delete robot records, ensuring
 * encapsulation of database operations related to robots.
 */
@Slf4j
@Service
public class RobotService implements WithCleanup {

    @Autowired
    private DSLContext db;

    public RobotEntity findRobotByName(String robotName, int ownerId) throws RuntimeException {
        RobotsRecord[] robotsRecords = db
            .selectFrom(ROBOTS)
            .where(ROBOTS.NAME.eq(robotName).and(ROBOTS.OWNER_ID.eq(ownerId)))
            .fetchArray();

        if (robotsRecords.length == 0) {
            throw new RobotNotFoundException(robotName);
        } else if (robotsRecords.length > 1) {
            log.error("The query returned more than one robot: returned {} robots with the name \"{}\"",
                robotsRecords.length, robotName);
            throw new RuntimeException("Multiple robots with the name \"" + robotName + "\" returned from the database");
        }

        return RobotEntity.of(robotsRecords[0]);
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

        db
            .insertInto(USER_ROBOT_RELATIONS)
            .columns(USER_ROBOT_RELATIONS.USER_ID, USER_ROBOT_RELATIONS.ROBOT_ID)
            .values(ownerId, robotsRecord.getRobotId())
            .execute();

        return RobotEntity.of(robotsRecord);
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
    public <T> void deleteAll(Collection<T> entities) {
        List<Integer> ids = entities.stream().map(entity -> {
            if (entity instanceof RobotEntity robotEntity) {
                return robotEntity.getId();
            }
            throw new IllegalArgumentException("entity must be an instance of RobotEntity class, provided: " + entity);
        }).toList();

        deleteAllByIds(ids);
    }
}
