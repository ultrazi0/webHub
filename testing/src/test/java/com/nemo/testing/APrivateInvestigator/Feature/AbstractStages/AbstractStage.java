package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.core.API.APIService;
import com.nemo.testing.core.API.WithBaseEndpoints;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
public abstract class AbstractStage<T extends AbstractStage<T>> extends Stage<T> implements WithBaseEndpoints {

    @ExpectedScenarioState
    protected CurrentStep currentStep;

    @Autowired
    protected APIService apiService;

    @FillerWord
    public T I() {
        return self();
    }

    @FillerWord
    public T I_am() {
        return self();
    }

    @FillerWord
    public T I_have() {
        return self();
    }

    @FillerWord
    public T a() {
        return self();
    }
}
