package com.nemo.testing.APrivateInvestigator.Feature.Sect.login;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractWhenStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class LoginWhenStage extends AbstractWhenStage<LoginWhenStage> {

    public LoginWhenStage login_endpoint() {
        request.to(LOGIN_ENDPOINT);

        return sendAndReturnSelf();
    }

    public LoginWhenStage logout_endpoint() {
        request.to(LOGOUT_ENDPOINT);

        return sendAndReturnSelf();
    }
}
