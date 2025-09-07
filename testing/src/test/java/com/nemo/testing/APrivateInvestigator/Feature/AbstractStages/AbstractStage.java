package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.core.API.APIService;
import com.nemo.testing.core.API.WithBaseEndpoints;
import com.nemo.testing.core.TypedClassInstanceMap;
import com.nemo.rexus.Decibel.UserEntity;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AbstractStage serves as a base class for defining stages in JGiven testing scenarios.
 * It provides utility methods and common functionality to be used across different stages.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
@JGivenStage
public abstract class AbstractStage<T extends AbstractStage<T>> extends Stage<T> implements WithBaseEndpoints {

    @ExpectedScenarioState
    protected CurrentStep currentStep;

    @ScenarioState
    protected UserEntity CURRENT_USER = null;

    /**
     * Map of the entities created in a test
     * */
    @ScenarioState
    protected TypedClassInstanceMap createdEntities;

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

    @FillerWord
    public T the() {
        return self();
    }
}
