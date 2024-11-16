package com.nemo.testing.APrivateInvestigator.Feature.Sect.register;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.core.Persistence.UserService;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class RegisterThenStage extends AbstractThenStage<RegisterThenStage> {

    @Autowired
    private UserService userService;

    @NestedSteps
    public RegisterThenStage response_is_correct(@Hidden String username) {
        return status_code_is(200)
            .and().body_has_user_id()
            .and().username_is(username);
    }

    public RegisterThenStage body_has_user_id() {
        validatableResponse.body("id", Matchers.instanceOf(Integer.class));

        return self();
    }

    public RegisterThenStage username_is(@Quoted String username) {
        validatableResponse.body("username", Matchers.equalTo(username));

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public RegisterThenStage new_user_$_is_created(String username) {
        assertThatCode(() -> createdEntities.addInstance(userService.getUserByUsername(username)))
            .as("Check if new user is created")
            .doesNotThrowAnyException();

        return self();
    }
}
