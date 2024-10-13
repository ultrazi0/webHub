package com.nemo.testing.Onion.Feature.AbstractStages;

import com.nemo.testing.Onion.Model.AbstractPage;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;

import java.util.function.Supplier;

public abstract class AbstractThenStage<T extends AbstractThenStage<T, P>, P extends AbstractPage> extends AbstractStage<T, P> {

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
