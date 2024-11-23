package com.nemo.testing.Onion.Model.User;

import com.nemo.testing.Onion.Model.AbstractPage;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Selenide.element;
import static com.codeborne.selenide.Selenide.open;

@Component
public class UserPage extends AbstractPage {

    public static final String uri = "user/{userId}";

    @Autowired
    private DeleteUserModal deleteUserModal;

    private final By USER_GREETING = By.xpath("//*[@id=\"root\"]/div/div/div[2]/h1");
    private final By EDIT_BUTTON = By.xpath("//*[@id=\"root\"]/div/div/div[2]/span/button[1]");
    private final By DELETE_BUTTON = By.xpath("//*[@id=\"root\"]/div/div/div[2]/span/button[2]");

    private Integer currentUserId = null;

    @Override
    public String uri() {
        if (currentUserId == null) {
            // If redirected to this page, in which case CURRENT_USER should not be null
            return uri.replace("{userId}", String.valueOf(currentStage.getCURRENT_USER().getId()));
        }
        return uri.replace("{userId}", currentUserId.toString());
    }

    @Override
    public void openPage() {
        openPage(currentStage.getCURRENT_USER().getId());
    }

    public void openPage(int id) {
        open(uri.replace("{userId}", String.valueOf(id)));
        currentUserId = id;
    }

    public final String getUserGreeting() {
        return element(USER_GREETING).getText();
    }

    /**
     * WARNING: this method does not wait
     * */
    public final boolean editButtonIsDisplayed() {
        return element(EDIT_BUTTON).isDisplayed();
    }

    public final void pressEditButton() {
        element(EDIT_BUTTON).click();
        element(EDIT_BUTTON).should(disappear);
    }

    public final void pressDeleteButton() {
        element(DELETE_BUTTON).click();
    }

    public final void pressCancelButtonInModal() {
        deleteUserModal.pressCancelButton();
        deleteUserModal.waitUntilModalIsClosed();
    }

    public final void pressDeleteButtonInModal() {
        deleteUserModal.pressDeleteButton();
        deleteUserModal.waitUntilModalIsClosed();
    }
}
