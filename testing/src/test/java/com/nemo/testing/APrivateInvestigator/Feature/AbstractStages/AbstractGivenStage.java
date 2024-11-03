package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.core.API.LoggedRequest;
import com.nemo.testing.core.API.Request;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public abstract class AbstractGivenStage<T extends AbstractGivenStage<T>> extends AbstractStage<T> {

    @ProvidedScenarioState
    protected Request request = new Request();

    @AfterScenario
    private void afterScenario() {
        apiService.reset();
    }

}
