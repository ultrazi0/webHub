package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.core.API.Request;
import com.nemo.testing.core.WithExtendedDescriptions;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.WithAssertions;
import org.hamcrest.Matchers;

/**
 * AbstractThenStage serves as a base class for defining "Then" stages in JGiven testing scenarios.
 * It extends AbstractStage to provide additional utility methods specifically for assertions.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractThenStage<T extends AbstractThenStage<T>> extends AbstractStage<T>
    implements WithAssertions, WithExtendedDescriptions {

    /**
     * {@link Response} acquired from the WHEN stage. It is not recommended to use it for validation -
     * for this use the parameter directly under. It is made protected with the idea that it may be
     * required to perform some checks directly on the {@link Response} entity, however, it is <b><u>NOT</u></b>
     * recommended to use this parameter
     *
     * @see AbstractThenStage#validatableResponse
     * */
    @ExpectedScenarioState
    protected Response response;

    /**
     * Use this parameter for any and all assertions you wish. Use validation provided by RestAssured
     * */
    protected ValidatableResponse validatableResponse;

    @BeforeStage
    private void setValidatableResponse() {
        validatableResponse = response.then();
    }

    /**
     * Asserts that the user is logged in as any user. It sends a separate request to the
     * user endpoint to be able to assert this.
     *
     * @see AbstractThenStage#logged_in_as(String)
     * */
    public T logged_in() {

        apiService.send(Request.createTo(USER_ENDPOINT))
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", Matchers.instanceOf(Integer.class))
            .body("username", Matchers.notNullValue());

        return self();
    }

    /**
     * Asserts that the user is logged in as a specified user. It sends a separate request to the
     * user endpoint to be able to assert this.
     * <p>It is recommended to use this method instead of {@link AbstractThenStage#logged_in()}</p>
     *
     * @param user what the username is the logged-in user should be
     * */
    public T logged_in_as(String user) {

        apiService.send(Request.createTo(USER_ENDPOINT))
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", Matchers.instanceOf(Integer.class))
            .body("username", Matchers.equalTo(user));

        return self();
    }

    @FillerWord
    public T get_an_error_that() {
        return self();
    }

    /**
     * Default realization. Asserts that the status code is {@code 200}
     * */
    public T response_is_correct() {
        validatableResponse.statusCode(200);

        return self();
    }
}
