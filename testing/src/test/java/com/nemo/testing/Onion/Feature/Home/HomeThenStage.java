package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Home.HomePage;
import com.nemo.testing.core.Persistence.RobotService;
import com.nemo.webHub.Decibel.UserEntity;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class HomeThenStage extends AbstractThenStage<HomeThenStage> {

    @ExpectedScenarioState
    private Set<Integer> createdRobots;
    @ExpectedScenarioState
    private UserEntity CURRENT_USER;

    @Autowired
    private HomePage homePage;
    @Autowired
    private RobotService robotService;

    @Override
    protected AbstractPage mainPage() {
        return homePage;
    }

    public HomeThenStage see_robot_$_as_a_card(@Quoted String robotName) {
        assertTakingScreenshotThat(homePage.thereIsARobotCardWithName(robotName),
            "Check the existence of a robot card")
            .isTrue();

        return self();
    }

    @ExtendedDescription("Checked in the database")
    public HomeThenStage robot_is_created(String robotName) {
        assertThatCode(() -> createdRobots.add(robotService.findRobotIdByName(robotName, CURRENT_USER.getId())))
            .as("Check in the database if the robot is created")
            .doesNotThrowAnyException();

        return self();
    }

    @As("see robot's info modal")
    public HomeThenStage see_robots_info_modal() {
        assertTakingScreenshotThat(homePage.infoModalIsVisible(),
            "Check if info modal is visible")
            .withFailMessage("Info modal is not visible")
            .isTrue();

        return self();
    }

    @NestedSteps
    public HomeThenStage all_the_data_is_correct(@Hidden String robotName) {
        return the_names_match(robotName)
            .and().the_ids_match()
            .and().the_owner_is_correct();
    }

    public HomeThenStage the_names_match(@Hidden String robotName) {
        assertTakingScreenshotThat(homePage.getRobotNameFromInfoModal(),
            "Check if the names match")
            .isEqualTo(robotName);

        return self();
    }

    public HomeThenStage the_ids_match() {
        assertThat(createdRobots)
            .as("Assert that only one robot has been created in this test")
            .hasSize(1);

        assertTakingScreenshotThat(homePage.getRobotIdFromInfoModal(),
            "Check if the ids match")
            .isEqualTo(createdRobots.iterator().next());

        return self();
    }

    public HomeThenStage the_owner_is_correct() {
        assertTakingScreenshotThat(homePage.getRobotOwnedByFromInfoModal(),
            "Check if the owner is correct")
            .isEqualTo(CURRENT_USER.getUsername());

        return self();
    }
}
