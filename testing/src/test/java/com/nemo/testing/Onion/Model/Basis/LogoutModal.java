package com.nemo.testing.Onion.Model.Basis;

import com.nemo.testing.Onion.Model.AbstractModal;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

/**
 * Represents a logout modal. When writing selectors, they should be relative to the {@code MODAL_DIALOG} element,
 * and then, instead of {@code Selenide.element()}, use the non-static {@code modalElement()}. <br />
 * This results in a cleaner and clearer code.
 *
 * @see AbstractModal#MODAL_DIALOG
 * @see AbstractModal#modalElement(By)
 * */
@Component
public class LogoutModal extends AbstractModal {

    private final By LOGOUT_BUTTON = By.xpath("./div/form/div[2]/button[2]");

    @Override
    public String modalTitle() {
        return "Log out";
    }

    public final void pressLogoutButton() {
        modalElement(LOGOUT_BUTTON).click();
    }
}
