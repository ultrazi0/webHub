package com.nemo.testing.APrivateInvestigator.Feature.User;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.core.API.Request;
import com.nemo.testing.core.Persistence.UserService;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class UserThenStage extends AbstractThenStage<UserThenStage> {

    @Autowired
    private UserService userService;

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public UserThenStage username_is_updated_to(@Quoted String newUsername) {
        assertThat(userService.getUserById(CURRENT_USER.getId()).getUsername())
                .as("Assert the username has been changed to %s", newUsername)
                .isEqualTo(newUsername);

        return self();
    }

    @Override
    @NestedSteps
    public UserThenStage response_is_correct() {
        return status_code_is(200)
                .and().id_is(CURRENT_USER.getId())
                .and().username_is(CURRENT_USER.getUsername());
    }

    @NestedSteps
    public UserThenStage response_is_correct(@Hidden String username) {
        return status_code_is(200)
                .and().id_is(CURRENT_USER.getId())
                .and().username_is(username);
    }

    public UserThenStage id_is(@Quoted int id) {
        validatableResponse.body("id", Matchers.equalTo(id));

        return self();
    }

    public UserThenStage username_is(@Quoted String username) {
        validatableResponse.body("username", Matchers.equalTo(username));

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public UserThenStage password_is_changed() {
        assertThat(userService.getUserById(CURRENT_USER.getId()).getPassword())
            .as("Assert the password has been changed")
            .isNotBlank()
            .isNotEqualTo(CURRENT_USER.getPassword());

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public UserThenStage user_$_is_deleted(@Quoted String username) {
        assertThatThrownBy(() -> userService.getUserByUsername(username))
            .as("Assert the user \"%s\" has been deleted", username)
            .isInstanceOf(UsernameNotFoundException.class);

        return self();
    }

    public UserThenStage logged_out() {

        apiService.send(Request.createTo(USER_ENDPOINT))
            .then()
            .statusCode(401);

        return self();
    }
}
