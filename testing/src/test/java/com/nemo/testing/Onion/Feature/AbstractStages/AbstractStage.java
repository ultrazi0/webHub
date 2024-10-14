package com.nemo.testing.Onion.Feature.AbstractStages;

import com.codeborne.selenide.Selenide;
import com.nemo.testing.Onion.DriverService;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.openqa.selenium.OutputType;
import org.springframework.beans.factory.annotation.Autowired;

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

    protected void addScreenshot(String title) {
        String base64 = Selenide.screenshot(OutputType.BASE64);

        if (base64 == null) throw new IllegalStateException("Driver does not support screenshots");

        currentStep.addAttachment(Attachment.fromBase64(base64, MediaType.PNG)
                .withTitle(title));
    }

    protected abstract AbstractPage mainPage();

    @FillerWord
    public T I_am() {
        return self();
    }

    @FillerWord
    public T I() {
        return self();
    }
}
