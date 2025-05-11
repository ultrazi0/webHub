package com.nemo.testing.Onion;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideWait;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Function;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.platform.commons.util.StringUtils.isBlank;

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
        @Value("${selenide.polling-interval}") long pollingInterval,
        @Value("${selenide.browser}") String browser,
        @Value("${selenide.browser-size}") String browserSize,
        @Value("${selenide.screenshots}") boolean screenshots,
        @Value("${selenide.headless}") boolean headless,
        @Value("${selenide.remote}") String remote) {

        Configuration.baseUrl = baseUrl;
        Configuration.timeout = timeout;
        Configuration.pollingInterval = pollingInterval;
        Configuration.browser = browser;
        Configuration.browserSize = browserSize;
        Configuration.screenshots = screenshots;
        Configuration.headless = headless;
        if (!isBlank(remote)) Configuration.remote = remote;

        Selenide.open();
        this.driver = getWebDriver();
    }

    /**
     * Waits until the given condition is met or the specified timeout is reached.
     *
     * <p>WARNING: Use <b><u>ONLY</u></b> when there is no other way - this is <b><u>NOT</u></b> recommended</p>
     *
     * @param condition The condition to wait for. This condition is checked repeatedly
     *                  until it returns successfully or the timeout is reached.
     * @param timeout The maximum amount of time in milliseconds to wait for the condition to be met.
     * @throws TimeoutException if the condition is not met within the specified timeout.
     */
    public final <T> void waitUntil(Function<WebDriver, T> condition, long timeout) throws TimeoutException {
            new SelenideWait(driver, timeout, Configuration.pollingInterval).until(condition);
    }

    /**
     * Waits until the given condition is met or the <i>default</i> timeout is reached.
     *
     * <p>WARNING: Use <b><u>ONLY</u></b> when there is no other way - this is <b><u>NOT</u></b> recommended</p>
     *
     * @param condition The condition to wait for. This condition is checked repeatedly
     *                  until it returns successfully or the timeout is reached.
     * @throws TimeoutException if the condition is not met within the specified timeout.
     */
    public final <T> void waitUntil(Function<WebDriver, T> condition) throws TimeoutException {
        waitUntil(condition, Configuration.timeout);
    }
}
