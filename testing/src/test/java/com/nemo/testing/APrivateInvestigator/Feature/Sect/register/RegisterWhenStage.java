package com.nemo.testing.APrivateInvestigator.Feature.Sect.register;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractWhenStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class RegisterWhenStage extends AbstractWhenStage<RegisterWhenStage> {

    public RegisterWhenStage register_endpoint() {
        request.to(REGISTER_ENDPOINT);

        return sendAndReturnSelf();
    }
}
