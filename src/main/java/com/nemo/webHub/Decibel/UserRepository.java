package com.nemo.webHub.Decibel;

import org.jooq.DSLContext;
import org.jooq.generated.tables.records.UsersRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import static org.jooq.generated.Tables.*;

@Repository
public class UserRepository {

    @Autowired
    private DSLContext db;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        UsersRecord newUser = db.update(USERS)
                .set(USERS.USERNAME, newUsername)
                .set(USERS.PASSWORD, encodePassword(newPassword))
                /*
                 * TODO: this prevents users from changing their password unless its encryption is bcrypt.
                 *  Unfortunately, I cannot think of any other way to change the password in one go.
                 *  So, unless I can think of another way, it shall stay like this.
                 *  Obviously, PasswordEncoder#matches requires an old password, in order to obtain which
                 *  one has to make another trip to the database, which would result in two separate trips,
                 *  which I think is not worth it.
                 *  Be careful: this line is used THRICE in all the three updateUser methods!
                 */
                .where(USERS.USER_ID.equal(id).and(USERS.PASSWORD.equal(encodePassword(oldPassword))))
                .returning()
                .fetchOne();

        if (newUser == null) {
            throw new UserNotFoundException(id);
        }

        return new UserEntity(newUser);
    }

    public UserEntity updateUserUsername(int id, String newUsername, String oldPassword) {
        UsersRecord newUser = db.update(USERS)
                .set(USERS.USERNAME, newUsername)
                .where(USERS.USER_ID.equal(id).and(USERS.PASSWORD.equal(encodePassword(oldPassword))))
                .returning()
                .fetchOne();

        if (newUser == null) {
            throw new UserNotFoundException(id);
        }

        return new UserEntity(newUser);
    }

    public UserEntity updateUserPassword(int id, String oldPassword, String newPassword) {
        UsersRecord newUser = db.update(USERS)
                .set(USERS.PASSWORD, encodePassword(newPassword))
                .where(USERS.USER_ID.equal(id).and(USERS.PASSWORD.equal(encodePassword(oldPassword))))
                .returning()
                .fetchOne();

        if (newUser == null) {
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

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
