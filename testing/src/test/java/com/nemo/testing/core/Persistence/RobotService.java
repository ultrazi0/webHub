package com.nemo.testing.core.Persistence;

import com.nemo.webHub.Decibel.RobotNotFoundException;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.jooq.generated.Tables.ROBOTS;
import static org.jooq.generated.Tables.USER_ROBOT_RELATIONS;

/**
 * The {@code RobotService} class provides functionalities to interact with the robot records
 * in the database. It includes methods to find, create, and delete robot records, ensuring
 * encapsulation of database operations related to robots.
 */
@Service
public class RobotService {

    @Autowired
    private DSLContext db;
    @Autowired
    private Logger log;

    public int findRobotIdByName(String robotName, int ownerId) throws RuntimeException {
        Record1<Integer>[] robotIds = db
            .select(ROBOTS.ROBOT_ID)
            .from(ROBOTS)
            .where(ROBOTS.NAME.eq(robotName).and(ROBOTS.OWNER_ID.eq(ownerId)))
            .fetchArray();

        if (robotIds.length == 0) {
            throw new RobotNotFoundException(robotName);
        } else if (robotIds.length > 1) {
            log.error("The query returned more than one robot: returned {} robots with the name \"{}\"",
                robotIds.length, robotName);
            throw new RuntimeException("Multiple robots with the name \"" + robotName + "\" returned from the database");
        }

        return robotIds[0].value1();
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

    public int createNewRobot(String robotName, int ownerId) {
        Integer robotId = db
            .insertInto(ROBOTS)
            .columns(ROBOTS.NAME, ROBOTS.OWNER_ID)
            .values(robotName, ownerId)
            .returningResult(ROBOTS.ROBOT_ID)
            .fetchOne(ROBOTS.ROBOT_ID);

        if (robotId == null) {
            throw new RuntimeException("No robots were created");
        }

        db
            .insertInto(USER_ROBOT_RELATIONS)
            .columns(USER_ROBOT_RELATIONS.USER_ID, USER_ROBOT_RELATIONS.ROBOT_ID)
            .values(ownerId, robotId)
            .execute();

        return robotId;
    }

    public void deleteRobotById(int id) {
        int deleted = db.deleteFrom(ROBOTS).where(ROBOTS.ROBOT_ID.equal(id)).execute();

        if (deleted < 1) {
            throw new RuntimeException(id + " not found");
        }
    }
}
