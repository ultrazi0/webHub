package com.nemo.testing.Onion;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideWait;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Function;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Service class responsible for configuring and managing the WebDriver instance for browser-based tests.
 * This class initializes the browser settings such as base URL, timeout, browser size, and other configurations
 * required for running the WebDriver.
 * <p>
 * This service is utilized by different components and stages to interact with the browser during test execution.
 * It opens the browser at the specified base URL and sets up the driver configuration as specified.
 */
@Service
public class DriverService {

    public final WebDriver driver;

    public DriverService(
        @Value("${selenide.base-url}") String baseUrl,
        @Value("${selenide.timeout}") long timeout,
        @Value("${selenide.browser}") String browser,
        @Value("${selenide.browser-size}") String browserSize,
        @Value("${selenide.screenshots}") boolean screenshots,
        @Value("${selenide.headless}") boolean headless) {

        Configuration.baseUrl = baseUrl;
        Configuration.timeout = timeout;
        Configuration.browser = browser;
        Configuration.browserSize = browserSize;
        Configuration.screenshots = screenshots;
        Configuration.headless = headless;

        Selenide.open();
        this.driver = getWebDriver();
    }

    public final <T> void assertingWaitUntil(Function<WebDriver, T> condition, String description, long timeout) {
        Assertions.assertThatCode(() ->
            new SelenideWait(driver, timeout, Configuration.pollingInterval).until(condition))
            .as(description)
            .doesNotThrowAnyException();
    }

    public final <T> void assertingWaitUntil(Function<WebDriver, T> condition, String description) {
        assertingWaitUntil(condition, description, Configuration.timeout);
    }
}
