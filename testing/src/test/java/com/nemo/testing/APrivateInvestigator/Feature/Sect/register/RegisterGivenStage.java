package com.nemo.testing.APrivateInvestigator.Feature.Sect.register;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.core.Persistence.UserService;
import com.nemo.webHub.Decibel.UserNotFoundException;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class RegisterGivenStage extends AbstractGivenStage<RegisterGivenStage> {

    @Autowired
    private UserService userService;

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public RegisterGivenStage account_$_does_not_exist(String username) {
        assumeThatCode(() -> userService.getUserByUsername(username))
            .as("User with username \"%s\" already exists", username)
            .isInstanceOf(UserNotFoundException.class);

        return self();
    }

    public RegisterGivenStage username(@Quoted String username) {
        request.formParam("username", username);

        return self();
    }

    public RegisterGivenStage password(@Quoted String password) {
        request.formParam("password", password);

        return self();
    }

    public RegisterGivenStage supply_correct_credentials(@Quoted String username, @Quoted String password) {
        return username(username)
            .and().password(password);
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public RegisterGivenStage user_$_exists(@Quoted String username, @Hidden String password) {
        try {
            createdEntities.addInstance(userService.createNewUser(username, password));
        } catch (DataAccessException ignored) {
            log.warn("User with username \"{}\" already exists, it will be deleted after this test", username);
            // Even if the user was not created in this test, it still uses a test username, and therefore should be deleted
            createdEntities.addInstance(userService.getUserByUsername(username));
        }

        return self();
    }

    public RegisterGivenStage supply_incorrect_credentials(String username, String password) {
        return supply_correct_credentials(username, password);
    }
}
