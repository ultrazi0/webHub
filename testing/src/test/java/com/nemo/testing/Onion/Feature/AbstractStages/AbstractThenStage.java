package com.nemo.testing.Onion.Feature.AbstractStages;

import com.nemo.rexus.Decibel.UserEntity;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.WithAssertions;

import java.util.Collection;
import java.util.Set;

import static com.codeborne.selenide.WebDriverRunner.url;

/**
 * AbstractThenStage serves as a base class for defining "Then" stages in JGiven testing scenarios.
 * It extends AbstractStage to provide additional utility methods specifically for assertions.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
public abstract class AbstractThenStage<T extends AbstractThenStage<T>> extends AbstractStage<T>
    implements WithAssertions {

    /**
     * Asserts that the user is logged in by verifying the login status on the main page and captures a screenshot if
     * the assertion fails. Throws an assertion error with an appropriate message if the user is not logged in.
     *
     * @return the current instance of the stage for method chaining
     */
    @SuppressWarnings("UnusedReturnValue")
    public T logged_in() {
        assertTakingScreenshotThat(isLoggedIn(), "Check if logged in")
            .withFailMessage("I am not logged in")
            .isTrue();
        return self();
    }

    /**
     * Asserts that the user is not logged in. If the user is logged in, it fails the assertion
     * with an appropriate error message and captures a screenshot for the report.
     *
     * @return the current instance of the stage for method chaining
     */
    @SuppressWarnings("UnusedReturnValue")
    public T not_logged_in() {
        assertTakingScreenshotThat(isLoggedIn(), "Check if not logged in")
            .withFailMessage("I am logged in, when I should not be")
            .isFalse();

        return self();
    }

    /**
     * Asserts that the current URL matches the expected page URI and captures a screenshot if the assertion fails.
     *
     * @param pageURI the expected relative URI of the page to be asserted
     */
    protected void assertOnCorrectPage(String pageURI) {
        assertTakingScreenshotThat(url(), "Check page redirect")
            .isEqualTo(fullUrlOf(pageURI));
    }

    /**
     * A wrapper around AssertJ {@code assertThat()} for string assertions that adds a screenshot to a JGiven report.
     *
     * @param string the string value to assert
     * @param description the <b>description</b> of the assertion,
     *                    do <u>NOT</u> write your error message here - use {@code withFailMessage(String)}
     *                    if you really wish to add one!
     * @param args arguments to format the description
     * @return {@link AbstractStringAssert}, allowing chaining of further checks and conditions
     */
    protected AbstractStringAssert<?> assertTakingScreenshotThat(String string, String description, Object... args) {
        return assertThat(string).as(addScreenshotToDescription(description, args));
    }

    /**
     * A wrapper around AssertJ {@code assertThat()} that adds screenshot to a JGiven report
     *
     * @param condition boolean condition to assert (can be {@code null})
     * @param description the <b>description</b> of the assertion,
     *                    do <u>NOT</u> write your error message here - use {@code withFailMessage(String)}
     *                    if you really wish to add one!
     * @param args arguments to format the description
     * @return {@link AbstractBooleanAssert>}, so that you can chain all the following checks and conditions
     * */
    protected AbstractBooleanAssert<?> assertTakingScreenshotThat(Boolean condition, String description, Object... args) {
        return assertThat(condition).as(addScreenshotToDescription(description, args));
    }

    /**
     * A wrapper around AssertJ {@code assertThat()} that adds screenshot to a JGiven report
     *
     * @param integer integer to assert
     * @param description the <b>description</b> of the assertion,
     *                    do <u>NOT</u> write your error message here - use {@code withFailMessage(String)}
     *                    if you really wish to add one!
     * @param args arguments to format the description
     * @return {@link AbstractIntegerAssert}, so that you can chain all the following checks and conditions
     * */
    protected AbstractIntegerAssert<?> assertTakingScreenshotThat(Integer integer, String description, Object... args) {
        return assertThat(integer).as(addScreenshotToDescription(description, args));
    }

    /**
     * A wrapper around AssertJ {@code assertThat()} that adds screenshot to a JGiven report
     *
     * @param collection collection to assert
     * @param description the <b>description</b> of the assertion,
     *                    do <u>NOT</u> write your error message here - use {@code withFailMessage(String)}
     *                    if you really wish to add one!
     * @param args arguments to format the description
     * @param <E> the type of elements in the collection
     * @return {@link  AbstractCollectionAssert}, so that you can chain all the following checks and conditions
     * */
    public <E> AbstractCollectionAssert<?, Collection<? extends E>, E, ObjectAssert<E>> assertTakingScreenshotThat(
        Collection<E> collection, String description, Object... args
    ) {
        return assertThat(collection).as(addScreenshotToDescription(description, args));
    }

    @Override
    void verifyCreatedUsersSetContains(Set<UserEntity> users, String... usernames) {
        assertThat(users)
            .as("Assert that users have been created")
            .isNotEmpty()
            .as("Assert that specified users exist")
            .extracting(UserEntity::getUsername)
            .contains(usernames);
    }
}
