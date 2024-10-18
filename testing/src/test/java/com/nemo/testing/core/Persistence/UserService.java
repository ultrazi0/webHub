package com.nemo.testing.core.Persistence;

import com.nemo.webHub.Decibel.UserEntity;
import com.nemo.webHub.Decibel.UserNotFoundException;
import com.nemo.webHub.Decibel.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Attempts to find a user by their username. Does <u>NOT</u> return the user
     *
     * @param username the username of the user to be found
     * @throws UserNotFoundException if a user with the given username is not found
     */
    public void tryToFindUserByUsername(String username) throws UserNotFoundException {
        userRepository.findUserByUsername(username);
    }

    public UserEntity createNewUser(String username, String password) {
        return userRepository.addNewUser(username, passwordEncoder.encode(password));
    }

    public void delete(String username) throws UserNotFoundException {
        userRepository.deleteUser(username);
    }
}
