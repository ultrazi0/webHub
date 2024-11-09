package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.core.API.Request;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.response.Response;

/**
 * AbstractWhenStage serves as a base class for defining "When" stages in JGiven testing scenarios.
 * It extends the AbstractStage class, enabling shared functionalities and behaviors specific to "When" actions.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
@JGivenStage
public abstract class AbstractWhenStage<T extends AbstractWhenStage<T>> extends AbstractStage<T> {

    /**
     * {@link Request} provided by the GIVEN stage
     * */
    @ExpectedScenarioState
    protected Request request;
    /**
     * Provides the response for the THEN stage. It is <b><u>NOT</u></b> recommended to touch it
     * */
    @ProvidedScenarioState
    private Response response;

    @FillerWord
    public T send_request_to() {
        return self();
    }

    /**
     * Sends the request formed in the GIVEN stage.
     *
     * @return current stage
     * */
    @Hidden
    protected T sendAndReturnSelf() {
        response = apiService.send(request);

        return self();
    }
}
