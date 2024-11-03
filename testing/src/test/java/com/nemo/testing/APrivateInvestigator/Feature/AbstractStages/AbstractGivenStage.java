package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.APrivateInvestigator.Model.TestRequest;
import com.nemo.testing.core.API.Request;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
public abstract class AbstractGivenStage<T extends AbstractGivenStage<T>> extends AbstractStage<T> {

    @ProvidedScenarioState
    protected Request request = new TestRequest();

    @BeforeStage
    public void beforeStage() {
        ((TestRequest) request).setCurrentStep(currentStep);
    }

    @AfterScenario
    private void afterScenario() {
        apiService.reset();
    }

}
