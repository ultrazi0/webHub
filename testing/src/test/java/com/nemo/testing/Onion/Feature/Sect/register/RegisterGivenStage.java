package com.nemo.testing.Onion.Feature.Sect.register;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Sect.RegisterPage;
import com.nemo.testing.core.Persistence.UserService;
import com.nemo.webHub.Decibel.UserNotFoundException;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@Slf4j
@SuppressWarnings("UnusedReturnValue")
class RegisterGivenStage extends AbstractGivenStage<RegisterGivenStage> {

    @Autowired
    private RegisterPage registerPage;

    @Autowired
    private UserService userService;

    @Override
    protected AbstractPage mainPage() {
        return registerPage;
    }

    public RegisterGivenStage on_register_page() {
        open(registerPage);

        return self();
    }

    @ExtendedDescription("Checked in the database")
    public RegisterGivenStage my_account_does_not_exist(@Hidden String username) {
        assumeThatThrownBy(() -> userService.getUserByUsername(username))
            .as("User with username \"%s\" already exists", username)
            .isInstanceOf(UserNotFoundException.class);

        return self();
    }

    @ExtendedDescription("Checked in the database")
    public RegisterGivenStage account_$_already_exists(@Quoted String username, @Hidden String password) {
        try {
            createdEntities.addInstance(userService.createNewUser(username, password));
        } catch (DataAccessException ignored) {
            // Even if the user was not created in this test, it still uses a test username, and therefore should be deleted
            createdEntities.addInstance(userService.getUserByUsername(username));
            log.warn("User with username \"{}\" already exists, it will be deleted after this test", username);
        }
        return self();
    }
}
