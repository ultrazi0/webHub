package com.nemo.rexus.Decibel;

import static org.jooq.generated.Tables.CUSTOM_COMMANDS;
import static org.jooq.generated.Tables.ROBOTS;
import static org.jooq.generated.Tables.USERS;
import static org.jooq.generated.Tables.USER_ROBOT_RELATIONS;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

import com.nemo.rexus.Commands.CommandType;
import com.nemo.rexus.Commands.CustomCommandType;
import com.nemo.rexus.Commands.StandardCommandType;
import com.nemo.rexus.User.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep3;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Records;
import org.jooq.SelectOnConditionStep;
import org.jooq.generated.tables.records.CustomCommandsRecord;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class RobotRepository {

	private final DSLContext db;

	public @NotNull Record2<@NotNull RobotsRecord, @NotNull String> findRobotById(int id) {
		return db.select(ROBOTS, USERS.USERNAME)
				.from(ROBOTS)
				.innerJoin(USERS).on(ROBOTS.OWNER_ID.equal(USERS.USER_ID))
				.where(ROBOTS.ROBOT_ID.equal(id))
				.fetchOptional()
				.orElseThrow(() -> new RobotNotFoundException(id));
	}

	@NotNull
	public Record4<RobotsRecord, String, List<User>, List<CustomCommandType>> findRobotDetailedByIdIfAllowed(int id, int userId) {
		return createSelectRobotQuery(userId)
				.where(ROBOTS.ROBOT_ID.equal(id).and(USER_ROBOT_RELATIONS.USER_ID.equal(userId)))
				.fetchOptional()
				.orElseThrow(() -> new RobotNotFoundException(id));
	}

	public int findRobotIdByRobotIdAndUserIdIfAllowed(int id, int userId) {
		return db
				.select(USER_ROBOT_RELATIONS.ROBOT_ID)
				.from(USER_ROBOT_RELATIONS)
				.where(USER_ROBOT_RELATIONS.ROBOT_ID.equal(id).and(USER_ROBOT_RELATIONS.USER_ID.equal(userId)))
				.fetchOptional()
				.orElseThrow(() -> new RobotNotFoundException(id))
				.value1();
	}

	@NotNull
	public RobotsRecord insertNewRobot(String name, int userId) {
		return db
				.insertInto(ROBOTS)
				.columns(ROBOTS.NAME, ROBOTS.OWNER_ID)
				.values(name, userId)
				.returning()
				.fetchOptional()
				.orElseThrow(() -> new RuntimeException("Newly inserted robot is null"));
	}

	/// Updates the robot only when the user is its owner
	@NotNull
	public RobotsRecord updateRobot(int id, String name, int userId) {
		return db.update(ROBOTS)
				.set(ROBOTS.NAME, name)
				.where(ROBOTS.ROBOT_ID.equal(id).and(ROBOTS.OWNER_ID.equal(userId)))
				.returning()
				.fetchOptional()
				.orElseThrow(() -> new RobotNotFoundException(id));
	}

	/// Completely replaces the custom commands of the robot (i.e., deletes old ones and inserts new ones)
	@NotNull
	@Transactional
	public Stream<CustomCommandsRecord> replaceCustomCommands(int robotId, Collection<CommandType> commandTypes) {
		db.deleteFrom(CUSTOM_COMMANDS).where(CUSTOM_COMMANDS.ROBOT_ID.equal(robotId)).execute();

		InsertValuesStep3<CustomCommandsRecord, Integer, String, String[]> insertQuery = db.insertInto(CUSTOM_COMMANDS)
				.columns(CUSTOM_COMMANDS.ROBOT_ID, CUSTOM_COMMANDS.COMMAND_TYPE, CUSTOM_COMMANDS.COMMAND_KEYS);

		for (CommandType commandType : commandTypes) {
			if (commandType instanceof StandardCommandType) {
				continue;
			}
			insertQuery = insertQuery.values(robotId, commandType.getCommandType(), commandType.getKeys());
		}

		return insertQuery.returning().fetchStream();
	}

	/**
	 * @deprecated deletes the robot no matter what, <u>NOT SAFE</u> - use {@link RobotRepository#deleteRobot(int id, int userId)}
	 *
	 */
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
	 *
	 */
	public void deleteRobot(int robotId, int userId) {
		int deleted = db
				.deleteFrom(USER_ROBOT_RELATIONS)
				.where(USER_ROBOT_RELATIONS.USER_ID.equal(userId).and(USER_ROBOT_RELATIONS.ROBOT_ID.equal(robotId)))
				.execute();

		if (deleted < 1) {
			throw new RobotNotFoundException(robotId);
		}
	}

	@NotNull
	public Stream<Record4<RobotsRecord, String, List<User>, List<CustomCommandType>>> getUserRobots(int userId) {
		return createSelectRobotQuery(userId)
				.where(USER_ROBOT_RELATIONS.USER_ID.equal(userId))
				.fetchStream();
	}

	public boolean shareRobot(int robotId, int robotOwnerId, Collection<String> usernames) {
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

	public boolean unshareRobot(int robotId, int robotOwnerId, Collection<Integer> userIds) {
		return db.deleteFrom(USER_ROBOT_RELATIONS)
				.where(USER_ROBOT_RELATIONS.RELATION_ID.in(
						select(USER_ROBOT_RELATIONS.RELATION_ID)
								.from(USER_ROBOT_RELATIONS)
								.innerJoin(ROBOTS).on(ROBOTS.ROBOT_ID.equal(USER_ROBOT_RELATIONS.ROBOT_ID).and(ROBOTS.OWNER_ID.equal(robotOwnerId)))
								.where(USER_ROBOT_RELATIONS.ROBOT_ID.equal(robotId).and(USER_ROBOT_RELATIONS.USER_ID.in(userIds)))
				)).execute() > 0;
	}

	@NotNull
	public Stream<Record2<Integer, String>> getSharedUsers(int robotId, int ownerId) {
		return db.select(USERS.USER_ID, USERS.USERNAME)
				.from(USERS)
				.innerJoin(USER_ROBOT_RELATIONS).using(USERS.USER_ID)
				.where(USER_ROBOT_RELATIONS.ROBOT_ID.equal(robotId))
				.and(USERS.USER_ID.notEqual(ownerId))
				.fetchStream();
	}

	@NotNull
	private SelectOnConditionStep<Record4<RobotsRecord, String, List<User>, List<CustomCommandType>>> createSelectRobotQuery(int userId) {
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
						).convertFrom(result -> result.map(Records.mapping(User::new))),
						multiset(
								select(CUSTOM_COMMANDS.COMMAND_TYPE, CUSTOM_COMMANDS.COMMAND_KEYS)
										.from(CUSTOM_COMMANDS)
										.where(CUSTOM_COMMANDS.ROBOT_ID.equal(ROBOTS.ROBOT_ID))
						).convertFrom(result -> result.map(Records.mapping(CustomCommandType::new)))
				)
				.from(ROBOTS)
				.innerJoin(USER_ROBOT_RELATIONS).using(ROBOTS.ROBOT_ID)
				.innerJoin(USERS).on(ROBOTS.OWNER_ID.equal(USERS.USER_ID));
	}
}
