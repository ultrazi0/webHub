package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Home.HomePage;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatCode;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class HomeWhenStage extends AbstractWhenStage<HomeWhenStage> {

    @Autowired
    private HomePage homePage;

    @Override
    protected AbstractPage mainPage() {
        return homePage;
    }

    @NestedSteps
    public HomeWhenStage add_a_new_robot(@Quoted String robotName) {
        return I().press_add_robot_button()
            .and().in_the_modal_I_enter_robot_name(robotName)
            .and().in_the_modal_I_press_add_robot_button()
            .and().waitUntilModalIsClosed();
    }

    public HomeWhenStage press_add_robot_button() {
        homePage.pressAddRobotButton();

        return self();
    }

    public HomeWhenStage in_the_modal_I_enter_robot_name(@Quoted String robotName) {
        homePage.enterRobotNameInTheModal(robotName);

        return self();
    }

    public HomeWhenStage in_the_modal_I_press_add_robot_button() {
        homePage.pressAddRobotButtonInTheModal();

        return self();
    }

    @Hidden
    public HomeWhenStage waitUntilModalIsClosed() {
        assertThatCode(() -> driverService.waitUntil(driver -> !homePage.anyModalIsVisible()))
            .as(addScreenshotToDescription("Wait until modal is closed"))
            .doesNotThrowAnyException();

        return self();
    }

    @ExtendedDescription("Needed to make sure that the card is displayed")
    public HomeWhenStage press_the_refresh_robots_button() {
        homePage.pressRefreshRobotsButton();

        return self();
    }

    public HomeWhenStage press_on_the_info_button_on_the_robot(@Quoted String robotName) {
        homePage.clickOnInfoButton(robotName);

        return self();
    }

    @NestedSteps
    public HomeWhenStage delete_the_robot(@Quoted String defaultRobotName) {
        return I().press_the_delete_button_on(defaultRobotName)
            .and().I_confirm_the_deletion_in_the_modal()
            .and().waitUntilModalIsClosed();
    }

    public HomeWhenStage press_the_delete_button_on(@Quoted String defaultRobotName) {
        homePage.clickOnDeleteButton(defaultRobotName);

        return self();
    }

    private HomeWhenStage I_confirm_the_deletion_in_the_modal() {
        homePage.pressDeleteButtonInDeleteModal();

        return self();
    }
}
