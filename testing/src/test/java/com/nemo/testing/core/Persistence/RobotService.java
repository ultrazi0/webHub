package com.nemo.testing.core.Persistence;

import com.nemo.webHub.Decibel.RobotNotFoundException;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.jooq.generated.Tables.ROBOTS;

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

    public int findRobotIdByName(String robotName) throws RobotNotFoundException {
        Record1<Integer>[] robotIds = db
            .select(ROBOTS.ROBOT_ID)
            .from(ROBOTS)
            .where(ROBOTS.NAME.eq(robotName))
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

    public int createNewRobot(String robotName) {
        return db
            .insertInto(ROBOTS)
            .columns(ROBOTS.NAME, ROBOTS.OWNER_ID)
            .values(robotName, 0)
            .returning(ROBOTS.ROBOT_ID)
            .execute();
    }

    public void deleteRobotById(int id) {
        int deleted = db.deleteFrom(ROBOTS).where(ROBOTS.ROBOT_ID.equal(id)).execute();

        if (deleted < 1) {
            throw new RuntimeException(id + " not found");
        }
    }
}
