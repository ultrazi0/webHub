package com.nemo.testing.core.Persistence;

import com.nemo.webHub.Decibel.UserEntity;
import com.nemo.webHub.Decibel.UserNotFoundException;
import com.nemo.webHub.Decibel.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Service class for managing user-related operations.
 */
@Slf4j
@Service
public class UserService implements WithCleanup {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public UserEntity createNewUser(String username, String password) {
        return userRepository.addNewUser(username, passwordEncoder.encode(password));
    }

    public void delete(String username) throws UserNotFoundException {
        userRepository.deleteUser(username);
    }

    public void deleteAllByUsername(Collection<String> usernames) {
        // Since it is not supposed that one should delete large amounts of users in one go,
        //  it does not have any sense to implement a convenient way that allows that.
        for (String username : usernames) {
            try {
                delete(username);
            } catch (UserNotFoundException ignored) {
                log.warn("User {} not found, proceeding as is", username);
            }
        }
    }

    @Override
    public void delete(Object entity) {
        if (entity instanceof UserEntity user) {
            delete(user.getUsername());
        }
        throw new IllegalArgumentException(
            "entity must be an instance of UserEntity, provided: " + entity);
    }

    @Override
    public <T> void deleteAll(Collection<T> entities) {
        List<String> usernames = entities.stream().map(entity -> {
            if (entity instanceof UserEntity userEntity) {
                return userEntity.getUsername();
            }
            throw new IllegalArgumentException("entity must be an instance of UserEntity class, provided: " + entity);
        }).toList();

        deleteAllByUsername(usernames);
    }
}
