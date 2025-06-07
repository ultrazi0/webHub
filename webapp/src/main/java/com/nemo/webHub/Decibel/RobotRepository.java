package com.nemo.webHub.Decibel;

import com.nemo.webHub.Robot.RobotService;
import com.nemo.webHub.User.User;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static org.jooq.generated.Tables.*;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

@Repository
@RequiredArgsConstructor
public class RobotRepository {

    private final DSLContext db;
    private final RobotService robotService;

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
        Record3<RobotsRecord, String, List<User>> robot = createSelectRobotQuery(userId)
            .where(ROBOTS.ROBOT_ID.equal(id).and(USER_ROBOT_RELATIONS.USER_ID.equal(userId)))
            .fetchOne();

        if (robot == null) {
            throw new RobotNotFoundException(id);
        }

        return new RobotEntity(robot.value1())
            .withOwnerName(robot.value2())
            .withIsOnline(checkAvailability(robot.value1().getRobotId()))
            .withSharedUsers(robot.value3());
    }

    @Deprecated
    @NotNull
    public RobotEntity findRobotByName(String name) {
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

    /**
     * @deprecated deletes the robot no matter what, <u>NOT SAFE</u> - use {@link RobotRepository#deleteRobot(int id, int userId)}
     * */
    @Deprecated
    public void deleteRobot(int id) {
        int deleted = db.deleteFrom(ROBOTS).where(ROBOTS.ROBOT_ID.equal(id)).execute();

        if (deleted < 1) {
            throw new RobotNotFoundException(id);
        }
    }

    /**
     * Removes user-robot relation.
     * If the user is also the robot's owner, the latter is deleted as well, thanks to a trigger.
     * */
    public void deleteRobot(int robotId, int userId) {
        int deleted = db
            .deleteFrom(USER_ROBOT_RELATIONS)
            .where(USER_ROBOT_RELATIONS.USER_ID.equal(userId).and(USER_ROBOT_RELATIONS.ROBOT_ID.equal(robotId)))
            .execute();

        if (deleted < 1) {
            throw new RobotNotFoundException(robotId);
        }
    }

    public RobotEntity[] getUserRobots(int userId) {
        Record3<RobotsRecord, String, List<User>>[] records = createSelectRobotQuery(userId)
            .where(USER_ROBOT_RELATIONS.USER_ID.equal(userId))
            .fetchArray();

        return Arrays.stream(records)
                .map(record -> new RobotEntity(record.value1())
                    .withOwnerName(record.value2())
                    .withIsOnline(checkAvailability(record.value1().getRobotId()))
                    .withSharedUsers(record.value3())
                ).toArray(RobotEntity[]::new);
    }

    public boolean shareRobot(int robotId, int robotOwnerId, List<String> usernames) {
        return db
            .insertInto(USER_ROBOT_RELATIONS)
            .columns(USER_ROBOT_RELATIONS.ROBOT_ID, USER_ROBOT_RELATIONS.USER_ID)
            .select(
                select(ROBOTS.ROBOT_ID, USERS.USER_ID)
                    .from(ROBOTS, USERS)
                    .where(ROBOTS.ROBOT_ID.equal(robotId))
                    .and(ROBOTS.OWNER_ID.equal(robotOwnerId))
                    .and(USERS.USERNAME.in(usernames))
            ).onConflictDoNothing().execute() > 0;
    }

    public boolean unshareRobot(int robotId, int robotOwnerId, List<Integer> userIds) {
        return db.deleteFrom(USER_ROBOT_RELATIONS)
            .where(USER_ROBOT_RELATIONS.RELATION_ID.in(
                select(USER_ROBOT_RELATIONS.RELATION_ID)
                    .from(USER_ROBOT_RELATIONS)
                    .innerJoin(ROBOTS).on(ROBOTS.ROBOT_ID.equal(USER_ROBOT_RELATIONS.ROBOT_ID).and(ROBOTS.OWNER_ID.equal(robotOwnerId)))
                    .where(USER_ROBOT_RELATIONS.ROBOT_ID.equal(robotId).and(USER_ROBOT_RELATIONS.USER_ID.in(userIds)))
            )).execute() > 0;
    }

    public List<User> getSharedUsers(int robotId, int ownerId) {
        Record2<Integer, String>[] users = db.select(USERS.USER_ID, USERS.USERNAME)
            .from(USERS)
            .innerJoin(USER_ROBOT_RELATIONS).using(USERS.USER_ID)
            .where(USER_ROBOT_RELATIONS.ROBOT_ID.equal(robotId))
            .and(USERS.USER_ID.notEqual(ownerId))
            .fetchArray();

        return Arrays.stream(users).map(user -> new User(user.value1(), user.value2())).toList();
    }

    @Deprecated
    public RobotEntity[] getAllRobots() {
        RobotsRecord[] robotsRecords = db.selectFrom(ROBOTS).fetchArray();
        return Arrays.stream(robotsRecords)
                .map(RobotEntity::new)
                .toArray(RobotEntity[]::new);

    }

    @NotNull
    private SelectOnConditionStep<Record3<RobotsRecord, String, List<User>>> createSelectRobotQuery(int userId) {
        return db.select(
                ROBOTS,
                USERS.USERNAME,
                multiset(
                    select(USERS.USER_ID, USERS.USERNAME)
                        .from(USER_ROBOT_RELATIONS)
                        .innerJoin(USERS).using(USERS.USER_ID)
                        .where(USER_ROBOT_RELATIONS.ROBOT_ID.equal(ROBOTS.ROBOT_ID))
                        .and(USER_ROBOT_RELATIONS.USER_ID.notEqual(ROBOTS.OWNER_ID))
                        .and(ROBOTS.OWNER_ID.equal(userId))
                ).convertFrom(result -> result.map(Records.mapping(User::new)))
            )
            .from(ROBOTS)
            .innerJoin(USER_ROBOT_RELATIONS).using(ROBOTS.ROBOT_ID)
            .innerJoin(USERS).on(ROBOTS.OWNER_ID.equal(USERS.USER_ID));
    }

    private boolean checkAvailability(int robotId) {
        return robotService.robotIsConnected(robotId);
    }
}
