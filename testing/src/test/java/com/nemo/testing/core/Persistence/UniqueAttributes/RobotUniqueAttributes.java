package com.nemo.testing.core.Persistence.UniqueAttributes;

import org.jooq.Condition;

import static org.jooq.generated.tables.Robots.ROBOTS;

public class RobotUniqueAttributes extends AbstractUniqueAttributes {

    private String robotName;
    private Integer ownerId;

    private RobotUniqueAttributes(int id) {
        super(id);
    }

    private RobotUniqueAttributes(String robotName, Integer ownerId) {
        this.robotName = robotName;
        this.ownerId = ownerId;
    }

    public static RobotUniqueAttributes byRobotId(int robotId) {
        return new RobotUniqueAttributes(robotId);
    }

    public static RobotUniqueAttributes byRobotNameAndOwnerId(String robotName, Integer ownerId) {
        return new RobotUniqueAttributes(robotName, ownerId);
    }

    @Override
    public boolean enoughAttributesProvided() {
        return idProvided() || (robotNameProvided() && ownerIdProvided());
    }

    @Override
    public Condition buildWhereClause() {
        if (enoughAttributesProvided()) {
            if (idProvided()) {
                return ROBOTS.ROBOT_ID.equal(id);
            } else if (robotNameProvided() && ownerIdProvided()) {
                return ROBOTS.NAME.equal(robotName).and(ROBOTS.OWNER_ID.equal(ownerId));
            }
        }
        throw new IllegalStateException("Robot does not have enough attributes to assert uniqueness");
    }

    public boolean robotNameProvided() {
        return robotName != null;
    }

    public boolean ownerIdProvided() {
        return ownerId != null;
    }
}
