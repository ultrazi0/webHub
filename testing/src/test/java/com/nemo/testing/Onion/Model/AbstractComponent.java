package com.nemo.testing.Onion.Model;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.nemo.testing.Onion.DriverService;
import com.nemo.testing.Onion.Feature.AbstractStages.AbstractStage;
import lombok.Setter;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.element;

/**
 * AbstractComponent serves as a base class providing shared functionalities
 * and dependencies to be used by derived components within the application.
 */
@Component
public abstract class AbstractComponent {

    @Autowired
    protected DriverService driverService;

    @Setter
    protected static AbstractStage<?> currentStage;

    /**
     * Wraps {@link com.codeborne.selenide.Selenide#element(By)} in order to provide custom wait and check functionality
     *
     * @see WaitAndSeeIfElement
     * */
    protected WaitAndSeeIfElement waitAndSeeIf(By locator) {
        return new WaitAndSeeIfElement(locator);
    }

    /**
     * Class for checks with built-in implicit waits, which is intended to replace Selenide's should methods
     * with methods that return booleans so that custom assertions and assumptions could be written.
     * It is essentially a wrapper around {@link SelenideElement}
     *
     * <p>
     *     WARNING: the existence of this class does not mean that IFs in the tests are allowed!
     *     Do <b><u>NOT</u></b> write IFs in the tests! This class exists only for custom assertions and assumptions
     * </p>
     * */
    protected static class WaitAndSeeIfElement {

        private final SelenideElement element;

        private WaitAndSeeIfElement(By locator) {
            this.element = element(locator);
        }

        /**
         * <p>
         *     WARNING: utmost care should be used with this method!
         * </p>
         *
         * Checks if element matches given condition (with the given timeout)
         * <ol>
         * <li>If matches, immediately returns {@code true}</li>
         * <li>If no, waits (up to given timeout), and if still no, returns {@code false}.</li>
         * </ol>
         *
         * Do <b><u>NOT</u></b> use this method to negate its result, as it will wait the whole timeout!
         * */
        public boolean becomes(WebElementCondition condition) {
            return element.is(condition, Duration.ofMillis(Configuration.timeout));
        }
    }

}
