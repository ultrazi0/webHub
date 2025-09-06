package com.nemo.testing.core.Persistence;

import com.nemo.testing.core.Persistence.UniqueAttributes.AbstractUniqueAttributes;
import com.nemo.testing.core.Persistence.UniqueAttributes.UserUniqueAttributes;
import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Decibel.UserNotFoundException;
import com.nemo.rexus.Decibel.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.generated.tables.records.UsersRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static org.jooq.generated.tables.Users.USERS;

/**
 * Service class for managing user-related operations.
 */
@Slf4j
@Service
public class UserService implements WithPersistence<UserEntity> {

    @Autowired
    private DSLContext db;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserEntity getEntityWith(AbstractUniqueAttributes uniqueAttributes) {
        if (uniqueAttributes instanceof UserUniqueAttributes userUniqueAttributes) {
            UsersRecord userRecord = db
                .selectFrom(USERS)
                .where(uniqueAttributes.buildWhereClause())
                .fetchOne();

            if (userRecord == null) throw new UserNotFoundException(userUniqueAttributes.toString());

            return UserEntity.of(userRecord);
        }
        throw new IllegalStateException("uniqueAttributes is not an instance of UserUniqueAttributes");
    }

    @Override
    public UserEntity createEntityFrom(Record record) {
        if (record instanceof UsersRecord r) {
            if (r.getRoles() == null) r.setRoles(new String[]{"USER"});
            if (r.getPassword() == null) r.setPassword(r.getUsername());
            r.setPassword(passwordEncoder.encode(r.getPassword()));

            UsersRecord usersRecord = db.insertInto(USERS)
                .set(record)
                .returning()
                .fetchOne();

            if (usersRecord == null) throw new RuntimeException("Newly created user is somehow null");

            return UserEntity.of(usersRecord);
        }
        throw new IllegalStateException("uniqueAttributes is not an instance of UserUniqueAttributes");
    }

    @Override
    public UserEntity getFrom(Record record) {
        if (record instanceof UsersRecord usersRecord) {
            if (usersRecord.getUserId() != null) {
                return getUserById(usersRecord.getUserId());
            } else if (usersRecord.getUsername() != null) {
                return getUserByUsername(usersRecord.getUsername());
            }
            throw new IllegalStateException("not enough unique parameters");
        }
        throw new IllegalStateException("record is not an instance of UsersRecord");
    }

    /**
     * Retrieves the UserEntity object of a user based on their username.
     *
     * @param username the username of the user whose entity is to be retrieved
     * @return the UserEntity of the user with the given username
     * @throws UserNotFoundException if no user with the given username is found
     */
    public UserEntity getUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findUserByUsername(username);
    }

    /**
     * Retrieves the UserEntity object of a user based on their id.
     *
     * @param id the id of the user whose entity is to be retrieved
     * @return the UserEntity of the user with the given id
     * @throws UserNotFoundException if no user with the given id is found
     */
    public UserEntity getUserById(int id) throws UserNotFoundException {
        return userRepository.findUserById(id);
    }

    public UserEntity createNewUser(String username, String password) {
        return userRepository.addNewUser(username, passwordEncoder.encode(password));
    }

    public void delete(String username) throws UserNotFoundException {
        userRepository.deleteUser(username);
    }

    public void delete(int id) throws UserNotFoundException {
        userRepository.deleteUser(id);
    }

    private void deleteAllById(Collection<Integer> ids) {
        // Since it is not supposed that one should delete large amounts of users in one go,
        //  it does not have any sense to implement a convenient way that allows that.
        for (int id : ids) {
            try {
                delete(id);
            } catch (UserNotFoundException ignored) {
                log.warn("User with id \"{}\" not found, proceeding as is", id);
            }
        }
    }

    @Override
    public void delete(Object entity) {
        if (entity instanceof UserEntity user) {
            delete(user.getId());
        }
        throw new IllegalArgumentException(
            "entity must be an instance of UserEntity, provided: " + entity);
    }

    @Override
    public void deleteAll(Collection<UserEntity> entities) {
        deleteAllById(entities.stream().map(UserEntity::getId).toList());
    }
}
