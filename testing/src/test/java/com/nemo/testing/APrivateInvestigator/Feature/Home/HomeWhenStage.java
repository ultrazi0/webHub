package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.APrivateInvestigator.Model.Endpoints.WithHomeEndpoints;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class HomeWhenStage extends AbstractWhenStage<HomeWhenStage> implements WithHomeEndpoints {

    public HomeWhenStage get_all_commands_endpoint() {
        request.to(COMMANDS_ENDPOINT);

        return sendAndReturnSelf();
    }

    public HomeWhenStage command_values_endpoint() {
        request.to(COMMAND_VALUES_ENDPOINT);

        return sendAndReturnSelf();
    }
}
