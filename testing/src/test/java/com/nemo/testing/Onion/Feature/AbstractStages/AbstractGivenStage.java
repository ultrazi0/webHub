package com.nemo.testing.Onion.Feature.AbstractStages;

import com.codeborne.selenide.Selenide;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.AbstractProtectedPage;
import com.tngtech.jgiven.annotation.AfterScenario;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.Assumptions;
import org.assertj.core.api.WithAssumptions;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.WebDriverRunner.url;

/**
 * The AbstractGivenStage class serves as a base stage for JGiven testing scenarios.
 * It extends the AbstractStage class and provides utility methods to ensure the correct
 * page is loaded and rendered within the testing framework.
 */
public abstract class AbstractGivenStage<T extends AbstractGivenStage<T>> extends AbstractStage<T>
    implements WithAssumptions {

    private static final String TEST_USER_USERNAME = "testUser";
    private static final String TEST_USER_PASSWORD = "testUserPassword";

    /**
     * Assumes that the current URL matches the main page URL.
     * This method verifies that the browser is currently on the main page by comparing the current URL
     * with the expected URL constructed from the base URL and the main page's URI.
     * If the URLs do not match, an assumption failure is triggered.
     */
    protected void assumeOnMainPage() {
        assumeThat(url())
            .as("Oops... wrong page :(")
            .isEqualTo(baseUrl + mainPage().uri());
    }

    /**
     * Assumes that the given page has been rendered.
     * Uses assumptions to ensure that the method `waitUntilRendered` does not throw any exceptions.
     *
     * @param page the page to check if it has been rendered
     */
    protected void assumeRendered(AbstractPage page) {
        Assumptions.assumeThatThrownBy(page::waitUntilRendered)
            .as("Check page rendered")
            .withFailMessage("Page has not been rendered!")
            .doesNotThrowAnyException();
    }

    /**
     * A wrapper around AssertJ {@code assumeThat()} that adds screenshot to a JGiven report
     *
     * @param condition boolean condition to assert
     * @param description the <b>description</b> of the assertion,
     *                    do <u>NOT</u> write your error message here - use {@code withFailMessage(String)}
     *                    if you really wish to add one!
     * @return {@code AbstractBooleanAssert<?>}, so that you can chain all the following checks and conditions
     * */
    protected AbstractBooleanAssert<?> assumeTakingScreenshotThat(boolean condition, String description) {
        return Assumptions.assumeThat(condition)
            .as(addScreenshotToDescription(description));
    }

    /**
     * Cleans up the browser state after a testing scenario.
     * <p>
     * This method performs the following actions:<br />
     * 1. Logs out the user if they are currently logged in.<br />
     * 2. Clears the browser's session storage.<br />
     * 3. Clears the browser's local storage.<br />
     * 4. Clears all cookies from the browser.<br />
     * 5. Refreshes the browser page.
     */
    @AfterScenario
    private void cleanup() {
        if (mainPage().isLoggedIn()) {
            mainPage().performLogout();
        }

        Selenide.sessionStorage().clear();
        Selenide.clearBrowserLocalStorage();
        Selenide.clearBrowserCookies();
        Selenide.refresh();
    }

    /**
     * An action. Ensures that the user is not currently logged in by checking the login status on the main page.
     * If the user is already logged in, an assumption failure with a specific message is triggered.
     *
     * @return the current instance (self) for method chaining
     */
    public T not_logged_in() {
        assumeThat(mainPage().isLoggedIn())
            .as("Check if already logged in")
            .withFailMessage("I am already logged in")
            .isFalse();

        return self();
    }

    /**
     * Logs in as the test user if the main page is a protected page.
     * This method ensures that if the main page requires login, the test user will be automatically logged in
     * before any further interactions.
     *
     * @return the current instance (self) for method chaining
     */
    public T test_user() {
        if (mainPage() instanceof AbstractProtectedPage) {
            AbstractProtectedPage mainPage = (AbstractProtectedPage) this.mainPage();
            mainPage.performLogin(TEST_USER_USERNAME, TEST_USER_PASSWORD);

            assumeThatCode(() -> driverService.waitUntil(driver -> mainPage.isLoggedIn()))
                .as(addScreenshotToDescription("Check if logged in"))
                .doesNotThrowAnyException();
        }

        return self();
    }
}
