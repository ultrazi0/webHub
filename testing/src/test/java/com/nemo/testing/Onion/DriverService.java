package com.nemo.testing.Onion;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

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
