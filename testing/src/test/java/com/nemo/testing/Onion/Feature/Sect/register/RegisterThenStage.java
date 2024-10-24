package com.nemo.testing.Onion.Feature.Sect.register;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Home.HomePage;
import com.nemo.testing.Onion.Model.Sect.RegisterPage;
import com.nemo.testing.core.Persistence.UserService;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class RegisterThenStage extends AbstractThenStage<RegisterThenStage> {

    @Autowired
    private RegisterPage registerPage;

    @Autowired
    private UserService userService;

    @Override
    protected AbstractPage mainPage() {
        return registerPage;
    }

    @ExtendedDescription("Checked in the database")
    public RegisterThenStage my_account_is_created(@Hidden String username) {
        assertThatNoException()
            .as("User with username \"%s\" was not created", username)
            .isThrownBy(() -> userService.getUserIdByUsername(username));

        return self();
    }

    public RegisterThenStage on_home_page() {
        assertOnCorrectPage(HomePage.uri);
        return self();
    }

    public RegisterThenStage see_a_name_has_been_taken_message() {
        assertTakingScreenshotThat(registerPage.getNameHasBeenTakenMessage(),
            "Name has been taken message check")
            .withFailMessage("No name has been taken message was found")
            .isEqualTo("This username is already taken");

        return self();
    }

    public RegisterThenStage see_a_passwords_do_not_match_message() {
        assertTakingScreenshotThat(registerPage.getPasswordsDoNotMatchMessage(),
            "Check passwords do not match")
            .withFailMessage("Passwords do not match")
            .isEqualTo("Passwords do not match");

        return self();
    }

    public RegisterThenStage register_button_is_disabled() {
        assertTakingScreenshotThat(registerPage.registerButtonIsDisabled(),
            "Check register button is disabled")
            .withFailMessage("Register button is not disabled")
            .isTrue();

        return self();
    }
}
