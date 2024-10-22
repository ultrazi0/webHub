package com.nemo.testing.Onion.Model;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.element;

/**
 * AbstractModal provides a base class for modal dialog components
 * within the application, extending the functionalities of AbstractFragment.
 * It encapsulates the common behavior and elements necessary for interacting
 * with modal dialogs.
 */
public class AbstractModal extends AbstractFragment {

    protected By MODAL_DIALOG = By.className("modal-dialog");
    protected By MODAL_TITLE = By.className("modal-title");
    protected By CLOSE_BUTTON = By.xpath(".//div[@class='modal-header']/button[@class='btn-close']");
    protected By CANCEL_BUTTON = By.xpath(".//div[@class='modal-footer']/button[@class='btn btn-secondary']");

    protected final SelenideElement modalElement(By selector) {
        return element(MODAL_DIALOG).find(selector);
    }

    public final String getModalTitle() {
        return modalElement(MODAL_TITLE).getText();
    }

    public final void pressCloseButton() {
        modalElement(CLOSE_BUTTON).click();
    }

    public final void pressCancelButton() {
        modalElement(CANCEL_BUTTON).click();
    }

    public final boolean isVisible() {
        return element(MODAL_DIALOG).exists();
    }
}
