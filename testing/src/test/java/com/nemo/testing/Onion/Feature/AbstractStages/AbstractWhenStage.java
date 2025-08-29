package com.nemo.testing.Onion.Feature.AbstractStages;

import com.nemo.testing.core.Persistence.PersistenceServiceMapper;
import com.nemo.testing.core.Persistence.UniqueAttributes.AbstractUniqueAttributes;
import com.nemo.testing.core.Persistence.UniqueAttributes.RobotUniqueAttributes;
import com.nemo.testing.core.Persistence.UniqueAttributes.UserUniqueAttributes;
import com.nemo.testing.core.Persistence.WithPersistence;
import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.UserEntity;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * AbstractWhenStage serves as a base class for defining "When" stages in JGiven testing scenarios.
 * It extends the AbstractStage class, enabling shared functionalities and behaviors specific to "When" actions.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
@Slf4j
@JGivenStage
public abstract class AbstractWhenStage<T extends AbstractWhenStage<T>> extends AbstractStage<T> {

    @Autowired
    private PersistenceServiceMapper persistenceServiceMapper;

    /**
     * Registers an entity created by the UI directly (for example, when a test registers a user)
     * */
    @Hidden
    protected <E> T registerEntity(Class<E> cls, String name) {
        WithPersistence<E> persistenceService = persistenceServiceMapper.getPersistenceService(cls);

        AbstractUniqueAttributes uniqueAttributes = null;
        if (RobotEntity.class.equals(cls)) {
            uniqueAttributes = RobotUniqueAttributes.byRobotNameAndOwnerId(name, CURRENT_USER.getId());
        } else if (UserEntity.class.equals(cls)) {
            uniqueAttributes = UserUniqueAttributes.byUsername(name);
        }

        try {
            createdEntities.addInstance(persistenceService.getEntityWith(uniqueAttributes));
        } catch (RuntimeException e) {
            log.error(
                "Cannot register entity with name \"{}\" of type {}, proceeding as is, but the test will most likely fail",
                name, cls.getName(), e);
        }

        return self();
    }

    /**
     * A wrapper around AssertJ {@code assertThat()} that adds screenshot to a JGiven report
     *
     * @param condition boolean condition to assert
     * @param description the <b>description</b> of the assertion,
     *                    do <u>NOT</u> write your error message here - use {@code withFailMessage(String)}
     *                    if you really wish to add one!
     * @return {@code AbstractBooleanAssert<?>}, so that you can chain all the following checks and conditions
     * */
    protected AbstractBooleanAssert<?> assertTakingScreenshotThat(boolean condition, String description, Object... args) {
        return Assertions.assertThat(condition).as(addScreenshotToDescription(description, args));
    }

    @Override
    void verifyCreatedUsersSetContains(Set<UserEntity> users, String... usernames) {
        throw new UnsupportedOperationException("This method is not supported in this stage");
    }
}
