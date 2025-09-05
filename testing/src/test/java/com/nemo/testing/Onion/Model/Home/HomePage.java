package com.nemo.testing.Onion.Model.Home;

import com.nemo.testing.Onion.Model.AbstractPage;
import jakarta.annotation.Nullable;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@Component
public class HomePage extends AbstractPage {
    public static final String uri = "";  // empty string, because baseUrl already contains the slash

    // Inject the modals
    @Autowired
    private AddRobotModal addRobotModal;
    @Autowired
    private RobotInfoModal robotInfoModal;
    @Autowired
    private DeleteRobotModal deleteRobotModal;
    @Autowired
    private EditRobotModal editRobotModal;

    // Greeting
    private final By USERNAME_GREETING = By.xpath("//div[@id=\"root\"]/div/div[1]/div/h1");

    // Add robot
    private final By ADD_ROBOT_BUTTON = By.className("robot-card-add");
    private final By REFRESH_ROBOTS_BUTTON = By.id("home-refresh-robots-button");

    // Robot cards (RELATIVE TO PARENT, THAT IS CARD)
    private final By ROBOT_CARDS = By.className("robot-card");
    private final By INFO_BUTTON = By.className("robot-card-infoButton");
    private final By DELETE_BUTTON = By.className("robot-card-closeButton");
    private final By EDIT_BUTTON = By.xpath("./div[contains(@class, 'card-footer')]/button");
    private final By LINK_TO_OWNER = By.xpath(".//p[@class='card-text']/a");

    private By ROBOT_CARD_BY(String robotName) {
        return By.xpath(String.format(
            "//div[contains(@class, 'robot-card') and ./div[@class='card-body']/div[contains(text(), '%s')]]", robotName
        ));
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public void openPage() {
        open(uri);
    }

    public final String getUsernameGreeting() {
        return element(USERNAME_GREETING).getText();
    }

    // *********************** //
    // Add robot functionality //
    // *********************** //

    /**
     * Warning: this method does NOT wait!
     * */
    public final boolean canSeeAddRobotButton() {
        return element(ADD_ROBOT_BUTTON).isDisplayed();
    }

    public final void pressAddRobotButton() {
        element(ADD_ROBOT_BUTTON).click();
    }

    public final void enterRobotNameInTheModal(String name) {
        addRobotModal.enterRobotName(name);
    }

    public final void pressAddRobotButtonInTheModal() {
        addRobotModal.pressAddRobotButton();
        addRobotModal.waitUntilModalIsClosed();
    }

    public final void pressRefreshRobotsButton() {
        element(REFRESH_ROBOTS_BUTTON).click();
    }

    // ***************************** //
    // Interactions with robot cards //
    // ***************************** //

    /**
     * Warning: this method does NOT wait!
     * */
    public final boolean thereAreNoRobotCards() {
        return elements(ROBOT_CARDS).isEmpty();
    }

    public final boolean thereIsARobotCardWithName(String robotName) {
        return waitAndSeeIf(ROBOT_CARD_BY(robotName)).becomes(visible);
    }

    /**
     * Warning: this method does NOT wait!
     * */
    public final boolean thereIsNoRobotCardWithName(String robotName) {
        return !element(ROBOT_CARD_BY(robotName)).isDisplayed();
    }

    public final void clickOnRobotCard(String robotName) {
        element(ROBOT_CARD_BY(robotName)).click();
    }

    public final void clickOnInfoButton(String robotName) {
        element(ROBOT_CARD_BY(robotName)).find(INFO_BUTTON).click();
    }

    public final void clickOnDeleteButton(String robotName) {
        element(ROBOT_CARD_BY(robotName)).find(DELETE_BUTTON).click();
    }

    public final boolean editButtonOnAnExistingCardIsDisplayed(String robotName) {
        By robotCard = ROBOT_CARD_BY(robotName);
        return waitAndSeeIf(robotCard).becomes(visible) && element(robotCard).find(EDIT_BUTTON).isDisplayed();
    }

    public final boolean editButtonOnAnExistingCardIsNotDisplayed(String robotName) {
        By robotCard = ROBOT_CARD_BY(robotName);
        return waitAndSeeIf(robotCard).becomes(visible) && !element(robotCard).find(EDIT_BUTTON).isDisplayed();
    }

    public final void clickOnEditButton(String robotName) {
        element(ROBOT_CARD_BY(robotName)).find(EDIT_BUTTON).click();
    }

    /// Warning: this method does NOT wait!
    public final String getTheNameOfTheRobotsOwner(String robotName) {
        return element(ROBOT_CARD_BY(robotName)).find(LINK_TO_OWNER).getText();
    }

    /// Warning: this method does NOT wait!
    public final String getTheRobotsOwnerHref(String robotName) {
        return element(ROBOT_CARD_BY(robotName)).find(LINK_TO_OWNER).getAttribute("href");
    }

    // ********** //
    // Info modal //
    // ********** //

    /**
     * Warning: this method does NOT wait!
     * */
    public final boolean infoModalIsVisible() {
        return robotInfoModal.isVisible();
    }

    public final String getRobotNameFromInfoModal() {
        return robotInfoModal.getRobotName();
    }

    public final int getRobotIdFromInfoModal() {
        return Integer.parseInt(robotInfoModal.getRobotId());
    }

    public final String getRobotPasswordFromInfoModal() {
        return robotInfoModal.getRobotPassword();
    }

    public final String getRobotOwnedByFromInfoModal() {
        return robotInfoModal.getRobotOwnedBy();
    }

    public final String getRobotCreatedAtFromInfoModal() {
        return robotInfoModal.getRobotCreatedAt();
    }

    public final String getRobotOnlineStatusFromInfoModal() {
        return robotInfoModal.getRobotOnlineStatus();
    }

    public final void pressHideShowSharedWithButtonInInfoModal() {
        robotInfoModal.pressHideShowSharedWithButton();
    }

    /// Warning: this method does NOT wait!
    public final boolean hideShowSharedWithButtonIsDisplayedInInfoModal() {
        return robotInfoModal.hideShowSharedWithButtonIsDisplayed();
    }

    public final void pressAddUserButtonInInfoModal() {
        robotInfoModal.pressAddUserButton();
    }

    public final String getSharedUserLinkUsernameInInfoModal(int rowIndex) {
        return robotInfoModal.getSharedUserLinkText(rowIndex);
    }

    public final String getSharedUserLinkHrefInInfoModal(int rowIndex) {
        return robotInfoModal.getSharedUserLinkHref(rowIndex);
    }

    public final void enterUsernameToShareWith(String username, int rowIndex) {
        robotInfoModal.enterUsernameToShareTheRobotWith(username, rowIndex);
    }

    public final void pressShareUserButton(int rowIndex) {
        robotInfoModal.pressShareUserButton(rowIndex);
    }

    public final void pressUnshareUserButtonFor(String username) {
        robotInfoModal.pressUnshareUserButtonFor(username);
    }

    public final boolean sharedUserRowHasDisappearedFor(String username) {
        return robotInfoModal.sharedUserRowHasDisappearedFor(username);
    }

    public final int getAmountOfUserRows() {
        return robotInfoModal.getAmountOfUserRows();
    }

    // ************ //
    // Delete modal //
    // ************ //
    public final void pressDeleteButtonInDeleteModal() {
        deleteRobotModal.pressDeleteButton();
        deleteRobotModal.waitUntilModalIsClosed();
    }

    // ********** //
    // Edit modal //
    // ********** //

    public final String getRobotNameFromEditModal() {
        return editRobotModal.getRobotName();
    }

    public final void enterNewRobotNameInEditModal(String robotName) {
        editRobotModal.enterRobotName(robotName);
    }

    public final void pressSaveButtonInEditModal() {
        editRobotModal.pressSaveRobotButton();
        editRobotModal.waitUntilModalIsClosed();
    }

    public final boolean saveButtonInEditModalIsDisabled() {
        return editRobotModal.saveRobotButtonIsDisabled();
    }

    @Nullable
    public final List<String> getCommandsInEditModal() {
        return editRobotModal.getCommands();
    }

    public final void removeCommandByName(String commandName) {
        editRobotModal.pressRemoveCommandButton(commandName);
    }

    public final void openCommandForm(String commandName) {
        editRobotModal.clickCommandByName(commandName);
    }

    public final boolean commandNameInputFieldIsVisible() {
        return editRobotModal.commandByNameIsDisplayed();
    }

    public final boolean enterCommandNameIsDisabled() {
        return editRobotModal.enterCommandNameIsDisabled();
    }

    public final String getTextFromTheCommandInputField() {
        return editRobotModal.getTextFromTheCommandInputField();
    }

    public final List<String> getKeysFromTheCommandForm() {
        return editRobotModal.getKeys();
    }

    public final void pressAddCommandButtonInEditModal() {
        editRobotModal.pressAddCommandButton();
    }

    public final void enterCommandNameInEditModal(String commandName) {
        editRobotModal.enterCommandName(commandName);
    }

    public final String getCommandInputFieldErrorMessage() {
        return editRobotModal.getCommandInputFieldErrorMessage();
    }

    public final void pressAddKeyButtonInEditModal() {
        editRobotModal.pressAddKeyButton();
    }

    public final boolean addKeyButtonIsHidden() {
        return editRobotModal.addKeyButtonIsHidden();
    }

    public final void enterLastAddedKey(String key) {
        editRobotModal.enterLastAddedKey(key);
    }

    public final void changeValueForKey(String oldKey, String newKey) {
        editRobotModal.changeValueForKey(oldKey, newKey);
    }

    public final void clickOnTheRemoveKeyButton(String key) {
        editRobotModal.clickOnTheRemoveKeyButton(key);
    }

    @Nullable
    public final Boolean removeButtonIsHiddenFor(String commandName) {
        return editRobotModal.removeButtonIsHiddenFor(commandName);
    }

}
