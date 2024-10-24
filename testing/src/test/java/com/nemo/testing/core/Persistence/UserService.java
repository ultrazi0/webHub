package com.nemo.testing.core.Persistence;

import com.nemo.webHub.Decibel.UserEntity;
import com.nemo.webHub.Decibel.UserNotFoundException;
import com.nemo.webHub.Decibel.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user-related operations.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Retrieves the user ID of a user based on their username.
     *
     * @param username the username of the user whose ID is to be retrieved
     * @return the user ID of the user with the given username
     * @throws UserNotFoundException if no user with the given username is found
     */
    public UserEntity getUserIdByUsername(String username) throws UserNotFoundException {
        return userRepository.findUserByUsername(username);
    }

    public UserEntity createNewUser(String username, String password) {
        return userRepository.addNewUser(username, passwordEncoder.encode(password));
    }

    public void delete(String username) throws UserNotFoundException {
        userRepository.deleteUser(username);
    }
}
