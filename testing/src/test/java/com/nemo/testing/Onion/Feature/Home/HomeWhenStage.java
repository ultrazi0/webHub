package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Home.HomePage;
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
}
