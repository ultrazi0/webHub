package com.nemo.testing.Onion.Feature.AbstractStages;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;

import java.util.function.Supplier;

/**
 * AbstractThenStage serves as a base class for defining "Then" stages in JGiven testing scenarios.
 * It extends AbstractStage to provide additional utility methods specifically for assertions.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
public abstract class AbstractThenStage<T extends AbstractThenStage<T>> extends AbstractStage<T> {

    private Supplier<String> addScreenshotToDescription(String description) {
        return () -> {
            addScreenshot(description);
            return description;
        };
    }

    protected AbstractStringAssert<?> assertThat(String string, String errorMessage) {
        return Assertions.assertThat(string).as(addScreenshotToDescription(errorMessage));
    }

    /**
     * A wrapper around AssertJ {@code assertThat()} that adds screenshot to a JGiven report
     * */
    protected AbstractBooleanAssert<?> assertThat(boolean condition, String errorMessage) {
        return Assertions.assertThat(condition).as(addScreenshotToDescription(errorMessage));
    }

}
