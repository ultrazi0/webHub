package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractGivenStage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
class HomeGivenStage extends AbstractGivenStage<HomeGivenStage> {

    public HomeGivenStage request_values_of_command(@Quoted String commandType) {
        request.queryParam("commandType", commandType);

        return self();
    }
}
