package com.nemo.webHub.Decibel;

import jakarta.annotation.PostConstruct;
import org.jooq.DSLContext;
import org.jooq.generated.tables.Users;
import org.jooq.generated.tables.records.UsersRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.jooq.generated.Tables.*;

@Repository
public class UserRepository {

    @Autowired
    private DSLContext db;

    public UserEntity findUserByUsername(String username) {
        UsersRecord user = db
                .selectFrom(USERS)
                .where(USERS.USERNAME.equal(username))
                .fetchOne();

        if (user == null) {
            throw new UserNotFoundException(username);
        }
        return new UserEntity(user);
    }

    public UserEntity addNewUser(String username, String password) {
        UsersRecord newUser = db
                .insertInto(USERS)
                .columns(USERS.USERNAME, USERS.PASSWORD, USERS.ROLES)
                .values(username, password, new String[]{"USER"})
                .returning()
                .fetchOne();

        if (newUser == null) {
            throw new RuntimeException("Newly created user is somehow null");
        }
        return new UserEntity(newUser);
    }

    public void deleteUser(int id) {
        int deleted = db.deleteFrom(USERS).where(USERS.USER_ID.equal(id)).execute();

        if (deleted < 1) {
            throw new UserNotFoundException(id);
        }
    }
}
