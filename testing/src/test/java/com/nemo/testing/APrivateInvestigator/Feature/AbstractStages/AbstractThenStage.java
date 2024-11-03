package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.core.API.Request;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;

public abstract class AbstractThenStage<T extends AbstractThenStage<T>> extends AbstractStage<T> {

    @ExpectedScenarioState
    protected Response response;
    protected ValidatableResponse validatableResponse;

    @BeforeStage
    private void setValidatableResponse() {
        validatableResponse = response.then();
    }

    public T logged_in() {

        apiService.sendGet(Request.createTo("/user"))
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", Matchers.notNullValue())
            .body("username", Matchers.notNullValue());

        return self();
    }
}
