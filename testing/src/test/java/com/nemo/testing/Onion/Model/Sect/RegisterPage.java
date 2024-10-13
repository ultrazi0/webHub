package com.nemo.testing.Onion.Model.Sect;

import com.nemo.testing.Onion.Model.AbstractPage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.element;

public class RegisterPage extends AbstractPage {
    public static final String uri = "register";

    private final By USERNAME_INPUT = By.id("formRegisterUsername");
    private final By PASSWORD_INPUT = By.id("formRegisterPassword");
    private final By REPEAT_PASSWORD_INPUT = By.id("formRegisterRepeatPassword");
    private final By REGISTER_BUTTON = By.xpath("//*[@id=\"root\"]/div/div/div[2]/form/button");
    private final By NAME_HAS_BEEN_TAKEN_MESSAGE = By.xpath("//form/div[1]/small");
    private final By PASSWORDS_DO_NOT_MATCH_MESSAGE = By.xpath("//form/div[3]/small");

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public final void openPage() {
        open(uri);
    }

    public final void enterCredentials(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        enterRepeatPassword(password);
    }

    public final void enterUsername(String username) {
        element(USERNAME_INPUT).setValue(username);
    }

    public final void enterPassword(String password) {
        element(password).setValue(password);
    }

    public final void enterRepeatPassword(String password) {
        element(REPEAT_PASSWORD_INPUT).setValue(password);
    }



}
