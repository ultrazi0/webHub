package com.nemo.testing.APrivateInvestigator.Feature.Sect;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractGivenStage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
public class LoginGivenStage extends AbstractGivenStage<LoginGivenStage> {

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
}
