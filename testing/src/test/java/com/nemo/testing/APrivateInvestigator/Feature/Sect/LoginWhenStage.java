package com.nemo.testing.APrivateInvestigator.Feature.Sect;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractWhenStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class LoginWhenStage extends AbstractWhenStage<LoginWhenStage> {

    public LoginWhenStage login_endpoint() {
        response = apiService.sendPost(request.to("/login"));

        return self();
    }

    public LoginWhenStage logout_endpoint() {
        response = apiService.sendPost(request.to("/logout"));

        return self();
    }
}
