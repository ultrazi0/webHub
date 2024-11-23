package com.nemo.testing.Onion.Feature.User.Edit;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.User.UserEditPage;
import com.nemo.testing.Onion.Model.User.UserPage;
import com.nemo.testing.core.Persistence.UserService;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class UserEditPageThenStage extends AbstractThenStage<UserEditPageThenStage> {

    @Autowired
    private UserEditPage userEditPage;
    @Autowired
    private UserPage userPage;
    @Autowired
    private UserService userService;

    @Override
    protected AbstractPage mainPage() {
        return userEditPage;
    }

    public UserEditPageThenStage see_my_profile_page() {
        assertOnCorrectPage(userPage.uri());

        return self();
    }

    @NestedSteps
    public UserEditPageThenStage username_is_changed_to(@Quoted String newUsername) {
        return username_is(newUsername)
            .and().I().see_$_as_my_username(newUsername);
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public UserEditPageThenStage username_is(@Quoted String username) {
        assertThat(userService.getUserById(CURRENT_USER.getId()).getUsername())
            .as("Assert username has been changed")
            .isEqualTo(username);

        return self();
    }

    public UserEditPageThenStage see_$_as_my_username(@Quoted String username) {
        assertTakingScreenshotThat(userPage.getUserGreeting(), "Assert see correct username")
            .contains("Hi there,")
            .contains(username);

        return self();
    }

    public UserEditPageThenStage my_username_is_prefilled() {
        assertTakingScreenshotThat(userEditPage.getUsernameInputValue(), "Assert my username is prefilled")
            .isEqualTo(CURRENT_USER.getUsername());

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public UserEditPageThenStage password_is_changed() {
        assertThat(userService.getUserById(CURRENT_USER.getId()).getPassword())
            .as("Assert password has been changed")
            .isNotEqualTo(CURRENT_USER.getPassword());

        return self();
    }
}
