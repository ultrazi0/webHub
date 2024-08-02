package com.nemo.webHub.Decibel;

import jakarta.validation.constraints.NotNull;
import org.jooq.*;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

import static org.jooq.generated.Tables.*;

@Repository
public class RobotRepository {

    @Autowired
    private DSLContext db;

    @NotNull
    public RobotEntity findRobotById(int id) {
        RobotsRecord robotsRecord = db
                .selectFrom(ROBOTS)
                .where(ROBOTS.ID.equal(id))
                .fetchOne();

        if (robotsRecord == null) {
            throw new RobotNotFoundException(id);
        }

        return new RobotEntity(
                robotsRecord.getId(),
                robotsRecord.getName(),
                robotsRecord.getCreatedAt()
        );
    }

    @NotNull
    public RobotEntity findRobotByName(String name) {;
        RobotsRecord robotsRecord = db
                .selectFrom(ROBOTS)
                .where(ROBOTS.NAME.equal(name))
                .fetchOne();

        if (robotsRecord == null) {
            throw new RobotNotFoundException(name);
        }

        return new RobotEntity(
                robotsRecord.getId(),
                robotsRecord.getName(),
                robotsRecord.getCreatedAt()
        );
    }

    public RobotEntity insertNewRobot(String name) {
        RobotsRecord newRobot = db
                .insertInto(ROBOTS)
                .columns(ROBOTS.NAME)
                .values(name)
                .returning()
                .fetchOne();

        if (newRobot == null) {
            throw new RuntimeException("Newly inserted robot is null");
        }

        return new RobotEntity(newRobot);
    }

    public RobotEntity updateRobot(int id, String name) {
        RobotsRecord robot = db.update(ROBOTS)
                .set(ROBOTS.NAME, name)
                .where(ROBOTS.ID.equal(id))
                .returning()
                .fetchOne();

        if (robot == null) {
            throw new RobotNotFoundException(id);
        }

        return new RobotEntity(robot);
    }

    public void deleteRobot(int id) {
        int deleted = db.deleteFrom(ROBOTS).where(ROBOTS.ID.equal(id)).execute();

        if (deleted < 1) {
            throw new RobotNotFoundException(id);
        }
    }

    public RobotEntity[] getAllRobots() {
        RobotsRecord[] robotsRecords = db.selectFrom(ROBOTS).fetchArray();
        return Arrays.stream(robotsRecords)
                .map(record -> new RobotEntity(record.getId(), record.getName(), record.getCreatedAt()))
                .toArray(RobotEntity[]::new);

    }
}
