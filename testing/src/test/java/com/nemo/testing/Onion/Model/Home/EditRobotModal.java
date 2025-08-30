package com.nemo.testing.Onion.Model.Home;

import com.codeborne.selenide.SelenideElement;
import com.nemo.testing.Onion.Model.AbstractModal;
import jakarta.annotation.Nullable;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;

@Component
public class EditRobotModal extends AbstractModal {

    private static final By ROBOT_NAME = By.id("formName");
    private static final By SAVE_ROBOT_BUTTON = By.xpath(".//div[@class='modal-footer']/button[@type='submit']");

    // Commands list
    private static final By ROBOT_COMMANDS_LIST = By.xpath(".//div[@class='robot-commands-details-list']/div[contains(@class, 'list-group')]");
    /// Is relative to {@link EditRobotModal#ROBOT_COMMANDS_LIST}
    private static final By ROBOT_COMMANDS = By.xpath("./button");
    /// Is relative to {@link EditRobotModal#ROBOT_COMMANDS_LIST}
    private static By ROBOT_COMMAND_BY(String commandName) {
        return By.xpath(String.format("./button[./span[contains(text(), '%s')]]", commandName));
    }
    /// Is relative to {@link EditRobotModal#ROBOT_COMMAND_BY(String)}
    private static final By ROBOT_COMMAND_REMOVE_BUTTON = By.xpath("./span[contains(@class, 'delete-button')]");

    private static final By ADD_ROBOT_COMMAND_BUTTON = By.xpath(".//div[@class='robot-commands-details-list']/button[contains(@class, 'add-command-type-button')]");

    // Edit command form
    private static final By EDIT_COMMAND_FORM_INPUT = By.xpath(".//div[@class='robot-command-edit-form-body']/div/input[@placeholder='Command']");
    private static final By EDIT_COMMAND_FORM_INPUT_ERROR_MESSAGE = By.xpath(".//div[@class='robot-command-edit-form-body']/div/small");
    private static final By EDIT_COMMAND_FORM_ADD_KEY_BUTTON = By.id("robot-command-edit-form-add-key-button");
    private static final By EDIT_COMMAND_FORM_KEYS_LIST = By.xpath(".//div[contains(@class, 'robot-command-edit-form-body-keys-list')]");
    private static final By EDIT_COMMAND_FORM_ALL_KEY_INPUTS = By.xpath("./div/input");
    private static final By EDIT_COMMAND_FORM_LAST_KEY_INPUT = By.xpath("./div[last()]/input");
    private static By EDIT_COMMAND_FORM_KEY_INPUT_BY_KEY(String key) {
        return By.xpath(String.format("./div/input[@value='%s']", key));
    }
    private static By EDIT_COMMAND_FORM_REMOVE_KEY_BUTTON_BY_KEY(String key) {
        return By.xpath(String.format("./div[./input[@value='%s']]/span[contains(@class, 'delete-button')]", key));
    }

    @Override
    public String modalTitle() {
        return "Edit robot";
    }

    public final String getRobotName() {
        return modalElement(ROBOT_NAME).getText();
    }

    public final void enterRobotName(String name) {
        modalElement(ROBOT_NAME).setValue(name);
    }

    public final void pressSaveRobotButton() {
        modalElement(SAVE_ROBOT_BUTTON).click();
    }

    public final boolean saveRobotButtonIsDisabled() {
        return waitAndSeeIf(SAVE_ROBOT_BUTTON).becomes(disabled);
    }

    // ************** //
    // Commands Block //
    // ************** //

    @Nullable
    public final List<String> getCommands() {
        if (waitAndSeeIf(ROBOT_COMMANDS_LIST).becomes(visible)) {
            return modalElement(ROBOT_COMMANDS_LIST).findAll(ROBOT_COMMANDS)
                .asFixedIterable().stream()
                .map(SelenideElement::getText)
                .toList();
        }
        return null;
    }

    public final void clickCommandByName(String commandName) {
        modalElement(ROBOT_COMMANDS_LIST).find(ROBOT_COMMAND_BY(commandName)).click();
    }

    public final void pressRemoveCommandButton(String commandName) {
        modalElement(ROBOT_COMMANDS_LIST).find(ROBOT_COMMAND_BY(commandName)).find(ROBOT_COMMAND_REMOVE_BUTTON).click();
    }

    public final boolean commandByNameIsDisplayed() {
        return waitAndSeeIf(EDIT_COMMAND_FORM_INPUT).becomes(visible);
    }

    public final boolean enterCommandNameIsDisabled() {
        return waitAndSeeIf(modalElement(EDIT_COMMAND_FORM_INPUT)).becomes(disabled);
    }

    public final String getTextFromTheCommandInputField() {
        return modalElement(EDIT_COMMAND_FORM_INPUT).getValue();
    }

    public final List<String> getKeys() {
        return modalElement(EDIT_COMMAND_FORM_KEYS_LIST).findAll(EDIT_COMMAND_FORM_ALL_KEY_INPUTS)
            .asFixedIterable().stream()
            .map(SelenideElement::getValue)
            .toList();
    }

    public final void pressAddCommandButton() {
        modalElement(ADD_ROBOT_COMMAND_BUTTON).click();
    }

    public final void enterCommandName(String commandName) {
        modalElement(EDIT_COMMAND_FORM_INPUT).setValue(commandName);
    }

    public final String getCommandInputFieldErrorMessage() {
        return modalElement(EDIT_COMMAND_FORM_INPUT_ERROR_MESSAGE).getText();
    }

    public final void pressAddKeyButton() {
        modalElement(EDIT_COMMAND_FORM_ADD_KEY_BUTTON).click();
    }

    public final boolean addKeyButtonIsHidden() {
        return waitAndSeeIf(modalElement(EDIT_COMMAND_FORM_ADD_KEY_BUTTON)).becomes(hidden);
    }

    public final void enterLastAddedKey(String key) {
        modalElement(EDIT_COMMAND_FORM_KEYS_LIST).find(EDIT_COMMAND_FORM_LAST_KEY_INPUT).setValue(key);
    }

    public final void changeValueForKey(String oldKey, String newKey) {
        modalElement(EDIT_COMMAND_FORM_KEYS_LIST).find(EDIT_COMMAND_FORM_KEY_INPUT_BY_KEY(oldKey)).setValue(newKey);
    }

    public final void clickOnTheRemoveKeyButton(String key) {
        modalElement(EDIT_COMMAND_FORM_KEYS_LIST).find(EDIT_COMMAND_FORM_REMOVE_KEY_BUTTON_BY_KEY(key)).click();
    }

    @Nullable
    public final Boolean removeButtonIsHiddenFor(String commandName) {
        if (waitAndSeeIf(ROBOT_COMMANDS_LIST).becomes(visible)) {
            return !modalElement(ROBOT_COMMANDS_LIST).find(ROBOT_COMMAND_BY(commandName)).find(ROBOT_COMMAND_REMOVE_BUTTON).isDisplayed();
        }
        return null;
    }
}
