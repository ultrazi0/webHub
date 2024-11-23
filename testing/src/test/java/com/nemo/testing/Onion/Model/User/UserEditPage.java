package com.nemo.testing.Onion.Model.User;

import com.nemo.testing.Onion.Model.AbstractPage;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Selenide.element;
import static com.codeborne.selenide.Selenide.open;

@Component
public class UserEditPage extends AbstractPage {

    public static final String uri = "user/edit";

    private final By USERNAME_INPUT = By.id("formEditUsername");
    private final By OLD_PASSWORD_INPUT = By.id("formOldPassword");
    private final By NEW_PASSWORD_INPUT = By.id("formNewPassword");
    private final By REPEAT_PASSWORD_INPUT = By.id("formNewPasswordRepeat");

    private final By CANCEL_BUTTON = By.xpath("//*[@id=\"root\"]/div/div/div[2]/form/button[1]");
    private final By SUBMIT_BUTTON = By.xpath("//*[@id=\"root\"]/div/div/div[2]/form/button[2]");

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public void openPage() {
        open(uri);
    }

    public final String getUsernameInputValue() {
        return element(USERNAME_INPUT).getValue();
    }

    public final void enterUsername(final String username) {
        element(USERNAME_INPUT).setValue(username);
    }

    public final void enterOldPassword(final String oldPassword) {
        element(OLD_PASSWORD_INPUT).setValue(oldPassword);
    }

    public final void enterNewPassword(final String newPassword) {
        element(NEW_PASSWORD_INPUT).setValue(newPassword);
    }

    public final void enterRepeatPassword(final String repeatPassword) {
        element(REPEAT_PASSWORD_INPUT).setValue(repeatPassword);
    }

    public final void pressCancelButton() {
        element(CANCEL_BUTTON).click();
    }

    public final void pressSubmitButton() {
        element(SUBMIT_BUTTON).click();
    }

    public final boolean isSubmitted() {
        return waitAndSeeIf(SUBMIT_BUTTON).becomes(hidden);
    }

}
