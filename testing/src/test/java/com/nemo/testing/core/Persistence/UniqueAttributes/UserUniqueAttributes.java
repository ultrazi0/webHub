package com.nemo.testing.core.Persistence.UniqueAttributes;

import org.jooq.Condition;

import static org.jooq.generated.tables.Users.USERS;

public class UserUniqueAttributes extends AbstractUniqueAttributes {

    private String username;

    private UserUniqueAttributes(Integer id) {
        super(id);
    }

    private UserUniqueAttributes(String username) {
        this.username = username;
    }

    public static UserUniqueAttributes byId(int id) {
        return new UserUniqueAttributes(id);
    }

    public static UserUniqueAttributes byUsername(String username) {
        return new UserUniqueAttributes(username);
    }

    @Override
    public boolean enoughAttributesProvided() {
        return idProvided() || usernameProvided();
    }

    @Override
    public Condition buildWhereClause() throws IllegalStateException {
        if (enoughAttributesProvided()) {
            if (idProvided()) {
                return USERS.USER_ID.equal(id);
            } else if (usernameProvided()) {
                return USERS.USERNAME.equal(username);
            }
        }
        throw new IllegalStateException("User does not have enough attributes to assert uniqueness");
    }

    public boolean usernameProvided() {
        return username != null;
    }
}
