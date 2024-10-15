package com.nemo.testing.Onion.Model;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.ex.ElementShould;
import com.nemo.testing.Onion.Model.Basis.LogoutModal;
import com.nemo.testing.Onion.Model.Basis.NavbarFragment;
import org.openqa.selenium.Cookie;
import org.springframework.beans.factory.annotation.Autowired;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.WebDriverRunner.driver;

/**
 * AbstractPage represents an abstract base class for all web pages in the application.
 * It extends AbstractComponent and includes common functionalities required for page interactions,
 * such as navigation, waiting for page rendering, and user authentication actions.
 */
public abstract class AbstractPage extends AbstractComponent {

    @Autowired
    protected NavbarFragment navbar;
    @Autowired
    private LogoutModal logoutModal;

    public abstract String uri();

    public abstract void openPage();

    protected void open(String url) {
        Selenide.open(url);
    }

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

    public boolean seeLoginButton() {
        return navbar.seeLoginButton();
    }

    public boolean isLoggedIn() {
        // The reason why it duplicates seeLoginButton() is that
        // the way it is checked whether the user is logged in can
        Cookie jSessionIdCookie = driver().getWebDriver().manage().getCookieNamed("JSESSIONID");
        return jSessionIdCookie != null && !navbar.seeLoginButton();
    }

    public void performLogout() {
        navbar.pressUserDropdownButton();
        navbar.pressUserDropdownLogoutButton();
        logoutModal.pressLogoutButton();
    }
}
