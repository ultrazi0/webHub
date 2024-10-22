package com.nemo.testing.Onion.Model.Home;

import com.nemo.testing.Onion.Model.AbstractProtectedPage;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.*;

@Component
public class HomePage extends AbstractProtectedPage {
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
    private final By ADD_ROBOT_BUTTON = By.xpath("//div[@id=\"root\"]/div/div[2]/div[1]/button");
    private final By REFRESH_ROBOTS_BUTTON = By.xpath("//div[@id=\"root\"]/div/div[2]/div[2]/button");

    // Robot cards (RELATIVE TO PARENT, THAT IS CARD)
    private final By ROBOT_CARDS = By.className("robotCard");
    private final By INFO_BUTTON = By.className("robotCard-infoButton");
    private final By DELETE_BUTTON = By.className("robotCard-closeButton");
    private final By EDIT_BUTTON = By.xpath("./div[contains(@class, 'card-footer')]/button");

    private By ROBOT_CARD_BY(String robotName) {
        return By.xpath(String.format(
            "//div[contains(@class, 'robotCard') and ./div[@class='card-body']/div[contains(text(), '%s')]]", robotName
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

    public final boolean anyModalIsVisible() {
        // In OR Java does not evaluate the next expression if the previous one is true,
        // thus, the following does not influence the performance. On the other hand, the
        // way modal element is found can be changes, so it makes sense to check the visibility
        // of all modals on this page
        return robotInfoModal.isVisible() || deleteRobotModal.isVisible() || editRobotModal.isVisible();
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

    /**
     * Warning: this method does NOT wait!
     * */
    public final boolean thereIsARobotCardWithName(String robotName) {
        return element(ROBOT_CARD_BY(robotName)).isDisplayed();
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

    /**
     * Warning: this method does NOT wait!
     * */
    public final boolean editButtonIsDisplayed(String robotName) {
        return element(ROBOT_CARD_BY(robotName)).find(EDIT_BUTTON).isDisplayed();
    }

    public final void clickOnEditButton(String robotName) {
        element(ROBOT_CARD_BY(robotName)).find(EDIT_BUTTON).click();
    }
}
