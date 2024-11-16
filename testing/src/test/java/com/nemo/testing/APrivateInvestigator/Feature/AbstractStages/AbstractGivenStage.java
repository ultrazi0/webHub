package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.APrivateInvestigator.Model.TestRequest;
import com.nemo.testing.core.API.Request;
import com.nemo.testing.core.Persistence.PersistenceServiceMapper;
import com.nemo.testing.core.Persistence.UserService;
import com.nemo.testing.core.TypedClassInstanceMap;
import com.nemo.testing.core.WithExtendedDescriptions;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.WithAssumptions;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The {@link AbstractGivenStage} serves as a base stage for JGiven testing scenarios.
 * It provides a protected {@code request} parameter of type {@link Request} that represents the
 * request to be tested. The class also automatically resets the {@link com.nemo.testing.core.API.APIService} after each scenario.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 * */
@Slf4j
@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractGivenStage<T extends AbstractGivenStage<T>> extends AbstractStage<T>
    implements WithAssumptions, WithExtendedDescriptions {

    private static final String TEST_USER_USERNAME = "testUser";
    private static final String TEST_USER_PASSWORD = "testUserPassword";

    @Autowired
    private UserService userService;
    @Autowired
    private PersistenceServiceMapper persistenceServiceMapper;

    @ProvidedScenarioState
    protected Request request = new TestRequest();

    @BeforeScenario
    public void beforeScenario() {
        createdEntities = new TypedClassInstanceMap();
    }

    @BeforeStage
    private void beforeStage() {
        ((TestRequest) request).setCurrentStep(currentStep);
    }

    @AfterScenario
    private void afterScenario() {
        apiService.reset();
        cleanupCreatedEntities();
    }

    protected void assumeLoggedIn(String username, String password) {
        assumeThat(apiService.login(username, password))
            .as("Log in", username, password)
            .withFailMessage("Cannot log in as \"%s\" with password \"%s\"", username, password)
            .isTrue();

        CURRENT_USER = userService.getUserByUsername(username);
    }

    public T test_user() {
        assumeLoggedIn(TEST_USER_USERNAME, TEST_USER_PASSWORD);

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public T user_$_exists(@Quoted String username, @Hidden String password) {
        try {
            createdEntities.addInstance(userService.createNewUser(username, password));
        } catch (DataAccessException ignored) {
            log.warn("User with username \"{}\" already exists, it will be deleted after this test", username);
            // Even if the user was not created in this test, it still uses a test username, and therefore should be deleted
            createdEntities.addInstance(userService.getUserByUsername(username));
        }

        return self();
    }

    private void cleanupCreatedEntities() {
        createdEntities.forEach((type, entitySet) -> {
            persistenceServiceMapper.getCleanupService(type).deleteAll(entitySet);
            entitySet.clear();
        });
    }
}
