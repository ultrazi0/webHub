package com.nemo.testing.Onion.Model;

import com.codeborne.selenide.Selenide;
import com.nemo.testing.Onion.Model.Basis.LogoutModal;
import com.nemo.testing.Onion.Model.Basis.NavbarFragment;
import org.assertj.core.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;

import static com.codeborne.selenide.Condition.visible;

public abstract class AbstractPage extends AbstractComponent {

    @Autowired
    protected NavbarFragment navbar;
    @Autowired
    private LogoutModal logoutModal;

    public abstract String uri();

    public abstract void openPage();

    protected void open(String url) {
        Selenide.open(url);
        // wait for the page to render

    }

    public void waitUntilRendered() throws Exception {
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
        // the way it is checked whether the user is logged in can change
        return !navbar.seeLoginButton();
    }

    public void performLogout() {
        navbar.pressUserDropdownButton();
        navbar.pressUserDropdownLogoutButton();
        logoutModal.pressLogoutButton();
    }
}
