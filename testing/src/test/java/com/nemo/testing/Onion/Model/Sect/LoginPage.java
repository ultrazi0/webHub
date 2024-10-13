package com.nemo.testing.Onion.Model.Sect;

import com.codeborne.selenide.Configuration;
import com.nemo.testing.Onion.Model.AbstractPage;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;


import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@Component
public class LoginPage extends AbstractPage {
    public static final String uri = "login";

    private final By USERNAME_INPUT = By.id("formLoginUsername");
    private final By PASSWORD_INPUT = By.id("formLoginPassword");
    private final By LOGIN_BUTTON = By.xpath("//form/button");
    private final By WRONG_CREDENTIALS_MESSAGE = By.xpath("//form/div[2]/small");

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public final void openPage() {
        open(uri);
    }

    public final void enterCredentials(String username, String password) {
        element(USERNAME_INPUT).setValue(username);
        element(PASSWORD_INPUT).setValue(password);
    }

    public final void pressSubmit() {
        element(LOGIN_BUTTON).click();
    }

    public final boolean shouldSeeWrongCredentialsMessage() {
        return element(WRONG_CREDENTIALS_MESSAGE).is(visible, Duration.ofMillis(Configuration.timeout));
    }
}
