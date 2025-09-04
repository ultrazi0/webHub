package com.nemo.testing.Onion.Model.Home;

import com.nemo.testing.Onion.Model.AbstractModal;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Condition.hidden;

@Component
public class RobotInfoModal extends AbstractModal {

    private static final By ROBOT_NAME = By.id("robot-info-modal-robot-name-span");
    private static final By ROBOT_ID = By.id("robot-info-modal-robot-id-span");
    private static final By ROBOT_PASSWORD = By.id("robot-info-modal-robot-password-span");
    private static final By ROBOT_OWNED_BY = By.id("robot-info-modal-robot-owner-username-span");
    private static final By ROBOT_CREATED_AT = By.id("robot-info-modal-robot-created-at-span");
    private static final By ROBOT_ONLINE_STATUS = By.id("robot-info-modal-robot-online-status-span");

    // Robot sharing
    private static final By ROBOT_HIDE_SHOW_SHARED_WITH_BUTTON = By.id("robot-info-modal-hide-show-shared-users-button");
    private static final By ROBOT_SHARE_ADD_USER_BUTTON = By.id("robot-info-shared-users-add-user");
    private static final By ROBOT_SHARED_USERS_ROWS = By.xpath(".//div[contains(@class, 'shared-users-block')]/div[contains(@class, 'list-group')]/div[contains(@class, 'list-group-item')]");
    private static final By ROBOT_SHARE_USERNAME_LINK_RELATIVE_TO_ROW = By.xpath("./a");
    private static final By ROBOT_SHARE_USERNAME_INPUT_RELATIVE_TO_ROW = By.xpath("./form/input");
    private static final By ROBOT_SHARE_SHARE_USER_BUTTON_RELATIVE_TO_ROW = By.xpath("./form/div[@class='share-user-actions']/span[contains(@class, 'share-button')]");
    private static final By ROBOT_SHARE_UNSHARE_USER_BUTTON_RELATIVE_TO_ROW = By.xpath("./span[contains(@class, 'delete-button')]");

    private static By ROBOT_SHARED_USER_ROW_BY(String username) {
        return By.xpath(
            ".//div[contains(@class, 'shared-users-block')]/div[contains(@class, 'list-group')]/div[contains(@class, 'list-group-item') and ./a[text()='%s']]"
                .formatted(username)
        );
    }

    @Override
    public String modalTitle() {
        return "Robot Info";
    }

    public final String getRobotName() {
        return modalElement(ROBOT_NAME).getText();
    }

    public final String getRobotId() {
        return modalElement(ROBOT_ID).getText();
    }

    public final String getRobotPassword() {
        return modalElement(ROBOT_PASSWORD).getText();
    }

    public final String getRobotOwnedBy() {
        return modalElement(ROBOT_OWNED_BY).getText();
    }

    public final String getRobotCreatedAt() {
        return modalElement(ROBOT_CREATED_AT).getText();
    }

    public final String getRobotOnlineStatus() {
        return modalElement(ROBOT_ONLINE_STATUS).getText();
    }

    public final void pressHideShowSharedWithButton() {
        modalElement(ROBOT_HIDE_SHOW_SHARED_WITH_BUTTON).click();
    }

    public final boolean hideShowSharedWithButtonIsDisplayed() {
        return modalElement(ROBOT_HIDE_SHOW_SHARED_WITH_BUTTON).isDisplayed();
    }

    public final void pressAddUserButton() {
        modalElement(ROBOT_SHARE_ADD_USER_BUTTON).click();
    }

    public final String getSharedUserLinkText(int index) {
        return modalElements(ROBOT_SHARED_USERS_ROWS).get(index).find(ROBOT_SHARE_USERNAME_LINK_RELATIVE_TO_ROW)
            .hover() // As a workaround to wait for the element to be visible
            .getText();
    }

    public final String getSharedUserLinkHref(int index) {
        return modalElements(ROBOT_SHARED_USERS_ROWS).get(index).find(ROBOT_SHARE_USERNAME_LINK_RELATIVE_TO_ROW).getAttribute("href");
    }

    public final void enterUsernameToShareTheRobotWith(String username, int index) {
        modalElements(ROBOT_SHARED_USERS_ROWS).get(index).find(ROBOT_SHARE_USERNAME_INPUT_RELATIVE_TO_ROW).setValue(username);
    }

    public final void pressShareUserButton(int index) {
        modalElements(ROBOT_SHARED_USERS_ROWS).get(index).find(ROBOT_SHARE_SHARE_USER_BUTTON_RELATIVE_TO_ROW).click();
    }

    public final void pressUnshareUserButtonFor(String username) {
        modalElement(ROBOT_SHARED_USER_ROW_BY(username)).find(ROBOT_SHARE_UNSHARE_USER_BUTTON_RELATIVE_TO_ROW).click();
    }

    public final boolean sharedUserRowHasDisappearedFor(String username) {
        return waitAndSeeIf(ROBOT_SHARED_USER_ROW_BY(username)).becomes(hidden);
    }

    public final int getAmountOfUserRows() {
        return modalElements(ROBOT_SHARED_USERS_ROWS).size();
    }

}
