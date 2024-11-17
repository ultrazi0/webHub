package com.nemo.testing.Onion.Model;

import com.codeborne.selenide.ex.ElementShould;
import com.nemo.testing.Onion.Model.Basis.NavbarFragment;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.elements;

/**
 * AbstractPage represents an abstract base class for all web pages in the application.
 * It extends AbstractComponent and includes common functionalities required for page interactions,
 * such as navigation, waiting for page rendering, and user authentication actions.
 */
public abstract class AbstractPage extends AbstractComponent {

    @Autowired
    protected NavbarFragment navbar;

    private static final By ALERTS = By.xpath("//div[@role='alert']");

    public abstract String uri();

    public abstract void openPage();

    /**
     * Waits until the logo element in the navbar is visible, indicating the page is rendered.
     *
     * @throws ElementShould if the logo element is not visible within the timeout duration
     */
    public void waitUntilRendered() throws ElementShould {
        navbar.getLogoElement().shouldBe(visible);
    }

    public String getUsernameFromNavbar() {
        return navbar.getCurrentUsername();
    }

    /**
     * WARNING: this method does NOT wait!
     * */
    public boolean seeSuccessAlert(String message) {
        return elements(ALERTS).filter(cssClass("alert-success")).findBy(exactText(message)).isDisplayed();
    }

    public boolean seeLoginButton() {
        return navbar.seeLoginButton();
    }
}
