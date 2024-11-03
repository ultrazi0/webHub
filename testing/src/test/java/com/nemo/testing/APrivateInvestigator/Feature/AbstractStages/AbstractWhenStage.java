package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.core.API.Request;
import com.tngtech.jgiven.annotation.*;
import io.restassured.response.Response;

public abstract class AbstractWhenStage<T extends AbstractWhenStage<T>> extends AbstractStage<T> {

    @ExpectedScenarioState
    protected Request request;
    @ProvidedScenarioState
    protected Response response;


    @FillerWord
    public T send_request_to() {
        return self();
    }
}
