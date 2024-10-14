package com.nemo.testing.Onion.Feature.AbstractStages;

import com.codeborne.selenide.Selenide;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.tngtech.jgiven.annotation.AfterScenario;
import org.assertj.core.api.Assumptions;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.assertj.core.api.Assumptions.assumeThat;

/**
 * The AbstractGivenStage class serves as a base stage for JGiven testing scenarios.
 * It extends the AbstractStage class and provides utility methods to ensure the correct
 * page is loaded and rendered within the testing framework.
 */
public abstract class AbstractGivenStage<T extends AbstractGivenStage<T>> extends AbstractStage<T> {

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
            .as("Page has not been rendered!")
            .doesNotThrowAnyException();
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

}
