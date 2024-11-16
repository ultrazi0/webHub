package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.Onion.Model.Home.HomePage;
import com.nemo.testing.core.Persistence.RobotService;
import com.nemo.webHub.Decibel.RobotNotFoundException;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@JGivenStage
@Slf4j
@SuppressWarnings("UnusedReturnValue")
class HomeGivenStage extends AbstractGivenStage<HomeGivenStage> {

    // TODO: refactor using the mapper
    @ProvidedScenarioState
    private final Set<Integer> createdRobots = new HashSet<>();

    @Autowired
    private HomePage homePage;
    @Autowired
    private RobotService robotService;

    @Override
    protected HomePage mainPage() {
        return homePage;
    }

    @AfterScenario
    private void deleteCreatedRobots() {
        for (Integer robotId : createdRobots) {
            try {
                robotService.deleteRobotById(robotId);
            } catch (RuntimeException ignored) {
                log.warn("Could not delete robot  with ID {} because it does not exist, proceeding as is", robotId);
            } finally {
                createdRobots.remove(robotId);
            }
        }
    }

    public HomeGivenStage on_home_page() {
        homePage.openPage();
        assumeOnMainPage();
        assumeRendered(homePage);

        return self();
    }

    public HomeGivenStage see_add_robot_button() {
        assumeTakingScreenshotThat(homePage.canSeeAddRobotButton(),
            "Check if can see add robot button")
            .withFailMessage("No add robot button - try logging in")
            .isTrue();

        return self();
    }

    @ExtendedDescription("Checked in the database")
    public HomeGivenStage robot_with_name_$_does_not_exist(@Quoted String robotName) {
        assumeThatThrownBy(() -> robotService.findRobotByName(robotName, CURRENT_USER.getId()))
            .as("Check if robot with name \"%s\" does not exist (name should be unique among all users)", robotName)
            .isInstanceOf(RobotNotFoundException.class);

        return self();
    }

    @NestedSteps
    @ExtendedDescription("Resolved in the database")
    public HomeGivenStage robot_with_name_$_exists(@Quoted String robotName) {
        return robot_with_name_$_does_not_exist(robotName)
            .and().robot_with_name_$_is_created(robotName);
    }

    @ExtendedDescription("Resolved in the database")
    public HomeGivenStage robot_with_name_$_is_created(@Quoted String robotName) {
        assumeThatCode(() -> createdRobots.add(robotService.createNewRobot(robotName, CURRENT_USER.getId()).getId()))
            .as("Create robot with name \"%s\"", robotName)
            .doesNotThrowAnyException();

        return self();
    }

    @NestedSteps
    public HomeGivenStage can_edit_robot(@Quoted String robotName) {
        return I().press_the_refresh_robots_button()
            .and().I().see_edit_robot_button_on(robotName);
    }

    @ExtendedDescription("Needed to make sure that the card is displayed")
    public HomeGivenStage press_the_refresh_robots_button() {
        homePage.pressRefreshRobotsButton();

        return self();
    }


    @ExtendedDescription("I see the button if I am the owner")
    public HomeGivenStage see_edit_robot_button_on(@Quoted String robotName) {
        assumeTakingScreenshotThat(homePage.editButtonOnAnExistingCardIsDisplayed(robotName),
            "Check if edit button on \"%s\" is visible", robotName)
            .isTrue();

        return self();
    }

    @ExtendedDescription("Checked in the database")
    public HomeGivenStage its_owner() {
        assumeThat(createdRobots)
            .as("Assume that only one robot has been created")
            .hasSize(1);

        assumeThat(robotService.getRobotOwnerIdByRobotId(createdRobots.iterator().next()))
            .as("Check if current user is the owner of the robot")
            .isEqualTo(CURRENT_USER.getId());

        return self();
    }

    public HomeGivenStage see_robot_$_as_a_card(@Quoted String robotName) {
        return press_the_refresh_robots_button()
            .and().assumeSeeRobotAsCard(robotName);
    }

    @Hidden
    private HomeGivenStage assumeSeeRobotAsCard(String robotName) {
        assumeTakingScreenshotThat(homePage.thereIsARobotCardWithName(robotName),
            "Assume there is a card with name \"%s\"", robotName)
            .withFailMessage("No card with name \"%s\" found", robotName)
            .isTrue();

        return self();
    }
}
