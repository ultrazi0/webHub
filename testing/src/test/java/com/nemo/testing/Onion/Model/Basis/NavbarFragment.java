package com.nemo.testing.Onion.Model.Basis;

import com.codeborne.selenide.SelenideElement;
import com.nemo.testing.Onion.Model.AbstractFragment;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.element;

@Component
public class NavbarFragment extends AbstractFragment {

    private final By NAVBAR_LOGO = By.xpath("//nav/div/a");

    // Not logged in
    private final By NAVBAR_LOGIN_BUTTON = By.xpath("//nav/div/div/div/button[2]");
    private final By NAVBAR_REGISTER_BUTTON = By.xpath("//nav/div/div/div/button[1]");

    // Logged in
    private final By NAVBAR_CURRENT_USERNAME = By.xpath("//nav/div/div/div/a");
    private final By NAVBAR_USER_DROPDOWN_BUTTON = By.id("user-dropdown");
    private final By NAVBAR_USER_DROPDOWN_LOGOUT_BUTTON = By.xpath("//nav/div/div/div/div/a");

    public SelenideElement getLogoElement() {
        return element(NAVBAR_LOGO);
    }

    // NOT LOGGED IN
    public void pressLoginButton() {
        element(NAVBAR_LOGIN_BUTTON).click();
    }

    public void pressRegisterButton() {
        element(NAVBAR_REGISTER_BUTTON).click();
    }

    @Deprecated
    public String getLoginButtonText() {
        return element(NAVBAR_LOGIN_BUTTON).getText();
    }

    // LOGGED IN
    public String getCurrentUsername() {
        return element(NAVBAR_CURRENT_USERNAME).getText();
    }

    public void pressUserDropdownButton() {
        element(NAVBAR_USER_DROPDOWN_BUTTON).click();
    }

    public void pressUserDropdownLogoutButton() {
        element(NAVBAR_USER_DROPDOWN_LOGOUT_BUTTON).click();
    }

    // CHECKS
    public boolean seeLoginButton() {
        return element(NAVBAR_LOGIN_BUTTON).exists();
    }
}
