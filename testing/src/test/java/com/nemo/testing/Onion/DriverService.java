package com.nemo.testing.Onion;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

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

    public DriverService() {

        Configuration.baseUrl = "http://localhost:3000/";
        Configuration.timeout = 2000;
        Configuration.browserSize = "1366x1024";
        Configuration.screenshots = false;
        Configuration.headless = false;

        Selenide.open();
        this.driver = getWebDriver();
    }
}
