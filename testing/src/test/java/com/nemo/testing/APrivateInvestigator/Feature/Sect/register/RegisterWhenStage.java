package com.nemo.testing.APrivateInvestigator.Feature.Sect.register;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractWhenStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class RegisterWhenStage extends AbstractWhenStage<RegisterWhenStage> {

    private static final String REGISTER_ENDPOINT = "/register";

    public RegisterWhenStage register_endpoint() {
        response = apiService.sendPost(request.to(REGISTER_ENDPOINT));

        return self();
    }
}
