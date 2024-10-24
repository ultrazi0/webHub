package com.nemo.testing.Onion.Feature.Sect.register;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Sect.RegisterPage;
import com.nemo.testing.core.Persistence.UserService;
import com.nemo.webHub.Decibel.UserNotFoundException;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class RegisterGivenStage extends AbstractGivenStage<RegisterGivenStage> {

    // Set of users that should be deleted after the scenario
    @ProvidedScenarioState
    private final Set<String> createdUsers = new HashSet<>();

    @Autowired
    private Logger log;

    @Autowired
    private RegisterPage registerPage;

    @Autowired
    private UserService userService;

    @Override
    protected AbstractPage mainPage() {
        return registerPage;
    }

    @AfterScenario
    private void deleteCreatedUsers() {
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

    public RegisterGivenStage on_register_page() {
        registerPage.openPage();
        assumeOnMainPage();
        assumeRendered(registerPage);

        return self();
    }

    @ExtendedDescription("Checked in the database")
    public RegisterGivenStage my_account_does_not_exist(@Hidden String username) {
        assumeThatThrownBy(() -> userService.getUserIdByUsername(username))
            .as("User with username \"%s\" already exists", username)
            .isInstanceOf(UserNotFoundException.class);

        return self();
    }

    @ExtendedDescription("Checked in the database")
    public RegisterGivenStage account_$_already_exists(@Quoted String username, @Hidden String password) {
        try {
            userService.createNewUser(username, password);
        } catch (DataAccessException ignored) {
            log.warn("User with username \"{}\" already exists, it will be deleted after this test", username);
        }
        // Even if the user was not created in this test, it still uses a test username, and therefore should be deleted
        createdUsers.add(username);
        return self();
    }
}
