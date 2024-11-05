package com.nemo.testing.APrivateInvestigator.Feature.Sect.register;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractThenStage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.hamcrest.Matchers;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class RegisterThenStage extends AbstractThenStage<RegisterThenStage> {

    @NestedSteps
    public RegisterThenStage response_is_correct(@Hidden String username) {
        return status_code_is(200)
            .and().body_has_user_id()
            .and().username_is(username);
    }

    public RegisterThenStage status_code_is(int status_code) {
        validatableResponse.statusCode(status_code);

        return self();
    }

    public RegisterThenStage body_has_user_id() {
        validatableResponse.body("id", Matchers.instanceOf(Integer.class));

        return self();
    }

    public RegisterThenStage username_is(@Quoted String username) {
        validatableResponse.body("username", Matchers.equalTo(username));

        return self();
    }

    public RegisterThenStage name_is_already_taken() {
        validatableResponse.body("error", Matchers.equalTo("Name already taken"));

        return self();
    }
}
