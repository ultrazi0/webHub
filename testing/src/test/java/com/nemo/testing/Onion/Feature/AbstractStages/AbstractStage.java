package com.nemo.testing.Onion.Feature.AbstractStages;

import com.codeborne.selenide.Selenide;
import com.nemo.testing.APrivateInvestigator.Model.Endpoints.WithSecurityEndpoints;
import com.nemo.testing.Onion.DriverService;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.core.API.APIService;
import com.nemo.testing.core.API.Request;
import com.nemo.testing.core.TypedClassInstanceMap;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.response.Response;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Supplier;

import static com.codeborne.selenide.WebDriverRunner.driver;

/**
 * AbstractStage serves as a base class for defining stages in JGiven testing scenarios.
 * It provides utility methods and common functionality to be used across different stages.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
@JGivenStage
public abstract class AbstractStage<T extends AbstractStage<T>> extends Stage<T> implements WithSecurityEndpoints {

    @Autowired
    protected DriverService driverService;
    @Autowired
    protected APIService apiService;

    @ExpectedScenarioState
    protected CurrentStep currentStep;
    @ScenarioState
    protected TypedClassInstanceMap createdEntities;

    /**
     * Provides the main page object to be used in the testing scenarios. This method should be
     * implemented by subclasses to return an instance of the main page of the current page class.
     *
     * @return an instance of AbstractPage representing the main page of the page class
     */
    protected abstract AbstractPage mainPage();

    /**
     * Captures a screenshot and adds it as an attachment to the current step.
     *
     * @param title the title to be used for the screenshot attachment
     * @throws IllegalStateException if the driver does not support screenshots
     */
    public void takeScreenshot(String title) {
        String base64 = Selenide.screenshot(OutputType.BASE64);

        if (base64 == null) throw new IllegalStateException("Driver does not support screenshots");

        currentStep.addAttachment(Attachment.fromBase64(base64, MediaType.PNG)
                .withTitle(title));
    }

    /**
     * Adds a screenshot to the description and returns a {@code Supplier<String>} that provides the description.
     * Is intended to be used in asserts and assumes
     *
     * @param description the description to be used for the screenshot
     * @return {@code Supplier<String>} that provides the description after taking a screenshot
     */
    protected Supplier<String> addScreenshotToDescription(String description, Object... args) {
        return () -> {
            takeScreenshot(description);
            return String.format(description, args);
        };
    }

    /**
     * Checks if the browser is logged in by sending an API request with the session cookie if present
     * @return true if logged in
     * */
    protected boolean isLoggedIn() {
        Request request = Request.createTo(USER_ENDPOINT);
        Cookie sessionIdCookie = driver().getWebDriver().manage().getCookieNamed(apiService.getSessionCookieName());
        if (sessionIdCookie != null) {
            request.sessionId(sessionIdCookie.getValue());
        }
        return apiService.send(request).getStatusCode() == 200;
    }

    @BeforeStage
    private void propagateCurrentStageToPage() {
        mainPage().setCurrentStage(self());
    }

    @FillerWord
    public T I_am() {
        return self();
    }

    @FillerWord
    public T I() {
        return self();
    }

    @FillerWord
    public T a() {
        return self();
    }
}
