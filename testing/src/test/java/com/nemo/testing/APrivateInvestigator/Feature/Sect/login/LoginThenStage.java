package com.nemo.testing.APrivateInvestigator.Feature.Sect.login;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.core.API.Request;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
class LoginThenStage extends AbstractThenStage<LoginThenStage> {

    public LoginThenStage credentials_are_incorrect() {
        validatableResponse.statusCode(401);

        return self();
    }

    public LoginThenStage not_logged_in() {

        apiService.send(Request.createTo(USER_ENDPOINT))
            .then()
            .statusCode(401);

        return self();
    }
}
