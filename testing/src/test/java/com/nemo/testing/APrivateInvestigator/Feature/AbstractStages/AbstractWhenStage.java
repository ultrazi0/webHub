package com.nemo.testing.APrivateInvestigator.Feature.AbstractStages;

import com.nemo.testing.APrivateInvestigator.Model.TestRequest;
import com.nemo.testing.core.API.Request;
import com.nemo.testing.core.Persistence.PersistenceServiceMapper;
import com.nemo.testing.core.Persistence.UniqueAttributes.AbstractUniqueAttributes;
import com.nemo.testing.core.Persistence.WithPersistence;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AbstractWhenStage serves as a base class for defining "When" stages in JGiven testing scenarios.
 * It extends the AbstractStage class, enabling shared functionalities and behaviors specific to "When" actions.
 *
 * <p>
 *     <b>IMPORTANT:</b> if you want you request to be sent, return {@link AbstractWhenStage#sendAndReturnSelf()}
 *     instead of {@link com.tngtech.jgiven.Stage#self()}
 * </p>
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
@Slf4j
@JGivenStage
public abstract class AbstractWhenStage<T extends AbstractWhenStage<T>> extends AbstractStage<T> {

    @Autowired
    private PersistenceServiceMapper persistenceServiceMapper;

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

        if (request instanceof TestRequest testRequest)
            return registerCreatedEntityIfNeededFrom(testRequest);

        return self();
    }

    /**
     * Registers an entity created by the API request if needed (i.e. both EntityType and UniqueAttributes are provided)
     *
     * @see TestRequest#getEntityType()
     * @see TestRequest#getUniqueAttributes()
     * */
    @Hidden
    private T registerCreatedEntityIfNeededFrom(TestRequest request) {
        if (request.needsEntityRegister()) {
            Class<?> entityType = request.getEntityType();
            AbstractUniqueAttributes uniqueAttributes = request.getUniqueAttributes();

            WithPersistence<?> persistenceService = persistenceServiceMapper.getPersistenceService(entityType);

            try {
                createdEntities.addInstance(persistenceService.getEntityWith(uniqueAttributes));
            } catch (RuntimeException e) {
                log.error(
                    "Cannot register entity of type {} from request {}, proceeding as is, but the test will most likely fail",
                    entityType.getName(), request, e);
            }
        }

        return self();
    }
}
