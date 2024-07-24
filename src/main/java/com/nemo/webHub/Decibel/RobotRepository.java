package com.nemo.webHub.Decibel;

import jakarta.annotation.Nullable;
import org.jooq.*;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.jooq.generated.Tables.*;

@Repository
public class RobotRepository {

    @Autowired
    private DSLContext db;

    @Nullable
    public RobotEntity findRobotById(int id) {
        RobotsRecord robotsRecord = db
                .selectFrom(ROBOTS)
                .where(ROBOTS.ID.equal(id))
                .fetchOne();

        if (robotsRecord == null) {
            return null;
        }

        return new RobotEntity(
                robotsRecord.getId(),
                robotsRecord.getName(),
                robotsRecord.getCreatedAt()
        );
    }

    @Nullable
    public RobotEntity findRobotByName(String name) {;
        RobotsRecord robotsRecord = db
                .selectFrom(ROBOTS)
                .where(ROBOTS.NAME.equal(name))
                .fetchOne();

        if (robotsRecord == null) {
            return null;
        }

        return new RobotEntity(
                robotsRecord.getId(),
                robotsRecord.getName(),
                robotsRecord.getCreatedAt()
        );
    }

    public void insertNewRobot(String name) {
        RobotsRecord newRobot = db.newRecord(ROBOTS);
        newRobot.setName(name);
        newRobot.store();
    }
}
