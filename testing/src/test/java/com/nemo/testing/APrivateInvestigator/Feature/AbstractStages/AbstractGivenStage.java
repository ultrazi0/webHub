package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.APrivateInvestigator.Model.TestRequest;
import com.nemo.testing.core.API.Request;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.assertj.core.api.Assumptions;

/**
 * The {@link AbstractGivenStage} serves as a base stage for JGiven testing scenarios.
 * It provides a protected {@code request} parameter of type {@link Request} that represents the
 * request to be tested. The class also automatically resets the {@link com.nemo.testing.core.API.APIService} after each scenario.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 * */
@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractGivenStage<T extends AbstractGivenStage<T>> extends AbstractStage<T> {

    private static final String TEST_USER_USERNAME = "testUser";
    private static final String TEST_USER_PASSWORD = "testUserPassword";

    @ProvidedScenarioState
    protected Request request = new TestRequest();

    @BeforeStage
    private void beforeStage() {
        ((TestRequest) request).setCurrentStep(currentStep);
    }

    @AfterScenario
    private void afterScenario() {
        apiService.reset();
    }

    protected void assumeLoggedIn(String username, String password) {
        Assumptions.assumeThat(apiService.login(username, password))
            .as("Log in", username, password)
            .withFailMessage("Cannot log in as \"%s\" with password \"%s\"", username, password)
            .isTrue();
    }

    public T test_user() {
        assumeLoggedIn(TEST_USER_USERNAME, TEST_USER_PASSWORD);

        return self();
    }
}
