package com.nemo.testing.Onion.Model;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.util.Objects;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Selenide.element;

/**
 * AbstractModal provides a base class for modal dialog components
 * within the application, extending the functionalities of AbstractFragment.
 * It encapsulates the common behavior and elements necessary for interacting
 * with modal dialogs.
 */
public abstract class AbstractModal extends AbstractFragment {

    protected By MODAL_DIALOG = By.className("modal-dialog");
    protected By MODAL_TITLE = By.className("modal-title");
    protected By CLOSE_BUTTON = By.xpath(".//div[@class='modal-header']/button[@class='btn-close']");
    protected By CANCEL_BUTTON = By.xpath(".//div[@class='modal-footer']/button[@class='btn btn-secondary']");

    public abstract String modalTitle();

    protected final SelenideElement modalElement(By selector) {
        return element(MODAL_DIALOG).find(selector);
    }

    protected final ElementsCollection modalElements(By selector) {
        return element(MODAL_DIALOG).findAll(selector);
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

    /**
     * Warning: this method does NOT wait!
     * */
    public boolean isVisible() {
        return element(MODAL_DIALOG).exists() && Objects.equals(getModalTitle(), modalTitle());
    }

    /**
     * Warning: this method does NOT wait!
     * */
    public boolean anyModalIsVisible() {
        return element(MODAL_DIALOG).exists();
    }

    public final void waitUntilModalIsClosed() {
        try {
            element(MODAL_DIALOG).should(disappear);
        } catch (AssertionError e) {
            currentStage.takeScreenshot("Modal is not closed");
            throw e;
        }
    }
}
