package com.nemo.testing.Onion.Feature.AbstractStages;

import com.codeborne.selenide.Selenide;
import com.nemo.testing.Onion.DriverService;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.openqa.selenium.OutputType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Supplier;

/**
 * AbstractStage serves as a base class for defining stages in JGiven testing scenarios.
 * It provides utility methods and common functionality to be used across different stages.
 *
 * @param <T> the type of the concrete stage that extends this abstract class
 */
@JGivenStage
public abstract class AbstractStage<T extends AbstractStage<T>> extends Stage<T> {

    @Autowired
    protected DriverService driverService;

    @ExpectedScenarioState
    protected CurrentStep currentStep;

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
