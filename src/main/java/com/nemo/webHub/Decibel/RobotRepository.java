package com.nemo.webHub.Decibel;

import com.nemo.webHub.Robot.RobotService;
import jakarta.validation.constraints.NotNull;
import org.jooq.*;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.jooq.generated.Tables.*;

@Repository
public class RobotRepository {

    @Autowired
    private DSLContext db;
    @Autowired
    private RobotService robotService;

    @NotNull
    public RobotEntity findRobotById(int id) {
        RobotsRecord robotsRecord = db
                .selectFrom(ROBOTS)
                .where(ROBOTS.ROBOT_ID.equal(id))
                .fetchOne();

        if (robotsRecord == null) {
            throw new RobotNotFoundException(id);
        }

        return new RobotEntity(robotsRecord);
    }

    @NotNull
    public RobotEntity findRobotByIdIfAllowed(int id, int userId) {
        Record6<Integer, String, UUID, OffsetDateTime, Integer, String> robot =
                db.select(ROBOTS.ROBOT_ID,
                                ROBOTS.NAME,
                                ROBOTS.PASSWORD,
                                ROBOTS.CREATED_AT,
                                ROBOTS.OWNER_ID,
                                USERS.USERNAME)
                        .from(ROBOTS)
                        .innerJoin(USER_ROBOT_RELATIONS).using(ROBOTS.ROBOT_ID)
                        .innerJoin(USERS).on(ROBOTS.OWNER_ID.equal(USERS.USER_ID))
                        .where(ROBOTS.ROBOT_ID.equal(id).and(USER_ROBOT_RELATIONS.USER_ID.equal(userId)))
                        .fetchOne();

        if (robot == null) {
            throw new RobotNotFoundException(id);
        }

        return new RobotEntity(
                robot.component1(),
                robot.component2(),
                robot.component3(),
                robot.component4(),
                robot.component5()
        ).setOwnerName(robot.component6()).setIsOnline(checkAvailability(robot.component1()));
    }

    @Deprecated
    @NotNull
    public RobotEntity findRobotByName(String name) {;
        RobotsRecord robotsRecord = db
                .selectFrom(ROBOTS)
                .where(ROBOTS.NAME.equal(name))
                .fetchOne();

        if (robotsRecord == null) {
            throw new RobotNotFoundException(name);
        }

        return new RobotEntity(robotsRecord);
    }

    public RobotEntity insertNewRobot(String name, int userId) {
        RobotsRecord newRobot = db
                .insertInto(ROBOTS)
                .columns(ROBOTS.NAME, ROBOTS.OWNER_ID)
                .values(name, userId)
                .returning()
                .fetchOne();

        if (newRobot == null) {
            throw new RuntimeException("Newly inserted robot is null");
        }
        db.insertInto(USER_ROBOT_RELATIONS)
                .columns(USER_ROBOT_RELATIONS.USER_ID, USER_ROBOT_RELATIONS.ROBOT_ID)
                .values(userId, newRobot.getRobotId())
                .execute();

        return new RobotEntity(newRobot);
    }

    @Deprecated
    public RobotEntity updateRobot(int id, String name) {
        // Updates the robot no matter what, NOT SAFE - use updateRobot(int id, String name, int userId)
        RobotsRecord robot = db.update(ROBOTS)
                .set(ROBOTS.NAME, name)
                .where(ROBOTS.ROBOT_ID.equal(id))
                .returning()
                .fetchOne();

        if (robot == null) {
            throw new RobotNotFoundException(id);
        }

        return new RobotEntity(robot);
    }

    public RobotEntity updateRobot(int id, String name, int userId) {
        // This method updates the robot only when the user is its owner
        RobotsRecord robot = db.update(ROBOTS)
                .set(ROBOTS.NAME, name)
                .where(ROBOTS.ROBOT_ID.equal(id).and(ROBOTS.OWNER_ID.equal(userId)))
                .returning()
                .fetchOne();

        if (robot == null) {
            throw new RobotNotFoundException(id);
        }

        return new RobotEntity(robot);
    }

    @Deprecated
    public void deleteRobot(int id) {
        // Deletes the robot no matter what, NOT SAFE - use deleteRobot(int id, int userId)
        int deleted = db.deleteFrom(ROBOTS).where(ROBOTS.ROBOT_ID.equal(id)).execute();

        if (deleted < 1) {
            throw new RobotNotFoundException(id);
        }
    }

    public void deleteRobot(int id, int userId) {
        // This method deletes the robot only when the user is its owner
        int deleted = db
                .deleteFrom(ROBOTS)
                .where(ROBOTS.ROBOT_ID.equal(id).and(ROBOTS.OWNER_ID.equal(userId)))
                .execute();

        if (deleted < 1) {
            throw new RobotNotFoundException(id);
        }
    }

    public RobotEntity[] getUserRobots(int userId) {
        Record6<Integer, String, UUID, OffsetDateTime, Integer, String>[] records = db.select(ROBOTS.ROBOT_ID,
                        ROBOTS.NAME,
                        ROBOTS.PASSWORD,
                        ROBOTS.CREATED_AT,
                        ROBOTS.OWNER_ID,
                        USERS.USERNAME
                )
                .from(ROBOTS)
                .innerJoin(USER_ROBOT_RELATIONS).using(ROBOTS.ROBOT_ID)
                .innerJoin(USERS).on(ROBOTS.OWNER_ID.equal(USERS.USER_ID))
                .where(USER_ROBOT_RELATIONS.USER_ID.equal(userId))
                .fetchArray();

        return Arrays.stream(records)
                .map(record -> new RobotEntity(record.component1(),
                        record.component2(),
                        record.component3(),
                        record.component4(),
                        record.component5()
                ).setOwnerName(record.component6()).setIsOnline(checkAvailability(record.component1()))
                ).toArray(RobotEntity[]::new);
    }

    public RobotEntity[] getAllRobots() {
        RobotsRecord[] robotsRecords = db.selectFrom(ROBOTS).fetchArray();
        return Arrays.stream(robotsRecords)
                .map(RobotEntity::new)
                .toArray(RobotEntity[]::new);

    }

    public boolean checkAvailability(int robotId) {
        return robotService.robotIsConnected(robotId);
    }
}
