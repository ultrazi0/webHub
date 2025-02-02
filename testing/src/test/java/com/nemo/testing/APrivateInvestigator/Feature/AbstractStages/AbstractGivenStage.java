package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.APrivateInvestigator.Model.TestRequest;
import com.nemo.testing.core.API.Request;
import com.nemo.testing.core.Persistence.PersistenceServiceMapper;
import com.nemo.testing.core.Persistence.UniqueAttributes.AbstractUniqueAttributes;
import com.nemo.testing.core.Persistence.UserService;
import com.nemo.testing.core.Persistence.WithPersistence;
import com.nemo.testing.core.TypedClassInstanceMap;
import com.nemo.testing.core.WithExtendedDescriptions;
import com.nemo.webHub.Decibel.UserEntity;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.WithAssumptions;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.generated.tables.records.UsersRecord;
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

        UsersRecord usersRecord = new UsersRecord();
        usersRecord.setUsername(username);
        usersRecord.setPassword(password);
        createEntity(UserEntity.class, usersRecord);

        assumeThat(apiService.login(username, password))
            .as("Log in", username, password)
            .withFailMessage("Cannot log in as \"%s\" with password \"%s\"", username, password)
            .isTrue();

        CURRENT_USER = userService.getUserByUsername(username);
    }

    /**
     * Automatically creates and registers for deletion an entity of the given type from the given record
     *
     * @param cls type of the entity
     * @param record database record to create DB entry from
     * */
    protected <E> void createEntity(Class<E> cls, Record record) {
        E createdEntity;
        WithPersistence<E> persistenceService = persistenceServiceMapper.getPersistenceService(cls);

        try {
            createdEntity = persistenceService.createEntityFrom(record);
            createdEntities.addInstance(cls, createdEntity);
        } catch (DataAccessException ignored) {
            // Even if the user was not created in this test, it still uses a test username, and therefore should be deleted
            createdEntity = persistenceService.getFrom(record);
            createdEntities.addInstance(cls, createdEntity);
            log.warn("Record {} already exists, it will be deleted after this test", record);
        }

        assumeThat(createdEntity)
            .as("Assume entity is created")
            .isNotNull();
    }

    public T test_user() {
        assumeLoggedIn(TEST_USER_USERNAME, TEST_USER_PASSWORD);

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public T user_$_exists(@Quoted String username, @Hidden String password) {
        UsersRecord usersRecord = new UsersRecord();
        usersRecord.setUsername(username);
        usersRecord.setPassword(password);

        createEntity(UserEntity.class, usersRecord);

        return self();
    }

    /**
     * Provides type of the entity to be registered when request is sent.
     * Use this to auto-delete entities created manually in the test (such as when you register a user using API)
     * */
    protected void setEntityToRegisterType(Class<?> type) {
        if (request instanceof TestRequest testRequest) {
            testRequest.setEntityType(type);
        } else {
            throw new IllegalStateException("Request must be of type TestRequest, provided: " + request.getClass());
        }
    }

    /**
     * Provides unique attributes (by which it can be deleted) of the to-be-created entity
     * Use this to auto-delete entities created manually in the test (such as when you register a user using API)
     * */
    protected void setEntityToRegisterUniqueAttributes(AbstractUniqueAttributes uniqueAttributes) {
        if (request instanceof TestRequest testRequest) {
            testRequest.setUniqueAttributes(uniqueAttributes);
        } else {
            throw new IllegalStateException("Request must be of type TestRequest, provided: " + request.getClass());
        }
    }

    private void cleanupCreatedEntities() {
        createdEntities.forEach((type, entitySet) -> {
            persistenceServiceMapper.getPersistenceService(type).deleteAll(entitySet);
            entitySet.clear();
        });
    }
}
