package com.nemo.testing.Onion.Feature.AbstractStages;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.WithAssertions;

import java.util.function.Supplier;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.WebDriverRunner.url;

/**
 * AbstractThenStage serves as a base class for defining "Then" stages in JGiven testing scenarios.
 * It extends AbstractStage to provide additional utility methods specifically for assertions.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
public abstract class AbstractThenStage<T extends AbstractThenStage<T>> extends AbstractStage<T>
    implements WithAssertions {

    @SuppressWarnings("UnusedReturnValue")
    public T logged_in() {
        assertTakingScreenshotThat(mainPage().isLoggedIn(), "Check if logged in")
            .withFailMessage("I am not logged in")
            .isTrue();
        return self();
    }

    @SuppressWarnings("UnusedReturnValue")
    public T not_logged_in() {
        assertTakingScreenshotThat(mainPage().isLoggedIn(), "Check if not logged in")
            .withFailMessage("I am logged in, when I should not be")
            .isFalse();

        return self();
    }

    protected void assertOnCorrectPage(String pageURI) {
        assertTakingScreenshotThat(url(), "Check page redirect")
            .withFailMessage("I am not redirected to the requested page")
            .isEqualTo(baseUrl + pageURI);
    }

    protected Supplier<String> addScreenshotToDescription(String description) {
        return () -> {
            addScreenshot(description);
            return description;
        };
    }

    /**
     * A wrapper around AssertJ {@code assertThat()} for string assertions that adds a screenshot to a JGiven report.
     *
     * @param string the string value to assert
     * @param description the <b>description</b> of the assertion,
     *                    do <u>NOT</u> write your error message here - use {@code withFailMessage(String)}
     * @return {@code AbstractStringAssert<?>}, allowing chaining of further checks and conditions
     */
    protected AbstractStringAssert<?> assertTakingScreenshotThat(String string, String description) {
        return assertThat(string).as(addScreenshotToDescription(description));
    }

    /**
     * A wrapper around AssertJ {@code assertThat()} that adds screenshot to a JGiven report
     *
     * @param condition boolean condition to assert
     * @param description the <b>description</b> of the assertion,
     *                    do <u>NOT</u> write your error message here - use {@code withFailMessage(String)}
     * @return {@code AbstractBooleanAssert<?>}, so that you can chain all the following checks and conditions
     * */
    protected AbstractBooleanAssert<?> assertTakingScreenshotThat(boolean condition, String description) {
        return assertThat(condition).as(addScreenshotToDescription(description));
    }

}
