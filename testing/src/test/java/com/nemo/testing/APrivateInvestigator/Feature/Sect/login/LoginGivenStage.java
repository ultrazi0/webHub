package com.nemo.testing.APrivateInvestigator.Feature.Sect.login;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.core.Persistence.UserService;
import com.nemo.webHub.Decibel.UserNotFoundException;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static com.nemo.testing.core.ExtendedDescriptions.CHECKED_IN_DATABASE;

@Slf4j
@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class LoginGivenStage extends AbstractGivenStage<LoginGivenStage> {

    @Autowired
    private UserService userService;

    private final Set<String> createdUsers = new HashSet<>();

    public LoginGivenStage username(@Quoted String username) {
        request.formParam("username", username);

        return self();
    }

    public LoginGivenStage password(@Quoted String password) {
        request.formParam("password", password);

        return self();
    }

    public LoginGivenStage correct_credentials(@Quoted String username, @Quoted String password) {
        return username(username)
            .and().password(password);
    }

    public LoginGivenStage incorrect_credentials(@Quoted String username, @Quoted String password) {
        return correct_credentials(username, password);
    }

    public LoginGivenStage logged_in_as_$_with_password(String username, String password) {
        assumeLoggedIn(username, password);

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public LoginGivenStage user_$_exists(@Quoted String username, @Hidden String password) {
        try {
            userService.createNewUser(username, password);
        } catch (DataAccessException ignored) {
            log.warn("User with username \"{}\" already exists, it will be deleted after this test", username);
        }
        // Even if the user was not created in this test, it still uses a test username, and therefore should be deleted
        createdUsers.add(username);
        return self();
    }

    @AfterScenario
    private void afterScenario() {
        for (String username : createdUsers) {
            try {
                userService.delete(username);
            } catch (UserNotFoundException ignored) {
                log.warn("Could not delete user \"{}\" because it does not exist, proceeding as is", username);
            } finally {
                createdUsers.remove(username);
            }
        }
    }
}
