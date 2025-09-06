package com.nemo.rexus.Decibel;

import org.jooq.DSLContext;
import org.jooq.generated.tables.records.UsersRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import static org.jooq.generated.Tables.USERS;

@Repository
public class UserRepository {

    private final DSLContext db;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRepository(DSLContext db, PasswordEncoder passwordEncoder) {
        this.db = db;
        this.passwordEncoder = passwordEncoder;
    }

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

    public UserEntity findUserById(int id) {
        UsersRecord user = db
                .selectFrom(USERS)
                .where(USERS.USER_ID.equal(id))
                .fetchOne();

        if (user == null) {
            throw new UserNotFoundException(id);
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

        Assert.notNull(newUser, "Newly created user is somehow null");
        return new UserEntity(newUser);
    }

    public UserEntity updateUser(int id, String newUsername, String oldPassword, String newPassword) {
        /*
        * This results in two trips to the database for every successful update request.
        * Unfortunately, because I can think of no other way to compare passwords without requesting
        * the user first, it shall stay like this.
        * */

        UserEntity user = findUserById(id);

        if (user == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserNotFoundException(id);
        }

        // Allow the users here only after the old password has matched with the existing password
        UsersRecord newUser = db.update(USERS)
                .set(USERS.USERNAME, newUsername)
                .set(USERS.PASSWORD, passwordEncoder.encode(newPassword))
                .where(USERS.USER_ID.equal(id))
                .returning()
                .fetchOne();

        if (newUser == null) {
            // Should never happen due to previous checks
            throw new UserNotFoundException(id);
        }

        return new UserEntity(newUser);
    }

    public UserEntity updateUserUsername(int id, String newUsername, String oldPassword) {
        /*
         * This results in two trips to the database for every successful update request.
         * Unfortunately, because I can think of no other way to compare passwords without requesting
         * the user first, it shall stay like this.
         * */

        UserEntity user = findUserById(id);

        if (user == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserNotFoundException(id);
        }

        // Allow the users here only after the old password has matched with the existing password
        UsersRecord newUser = db.update(USERS)
                .set(USERS.USERNAME, newUsername)
                .where(USERS.USER_ID.equal(id))
                .returning()
                .fetchOne();

        if (newUser == null) {
            // Should never happen due to previous checks
            throw new UserNotFoundException(id);
        }

        return new UserEntity(newUser);
    }

    public void deleteUser(int id) {
        int deleted = db.deleteFrom(USERS).where(USERS.USER_ID.equal(id)).execute();

        if (deleted < 1) {
            throw new UserNotFoundException(id);
        }
    }

    public void deleteUser(String username) {
        int deleted = db.deleteFrom(USERS).where(USERS.USERNAME.equal(username)).execute();

        if (deleted < 1) {
            throw new UserNotFoundException(username);
        }
    }
}
