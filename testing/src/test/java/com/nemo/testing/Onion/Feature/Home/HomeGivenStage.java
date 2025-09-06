package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.Onion.Model.Home.HomePage;
import com.nemo.testing.core.Formatters.CustomCommandTypeArrayFormatter;
import com.nemo.testing.core.Persistence.RobotService;
import com.nemo.rexus.Commands.CustomCommandType;
import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Decibel.RobotNotFoundException;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@JGivenStage
@Slf4j
@SuppressWarnings("UnusedReturnValue")
class HomeGivenStage extends AbstractGivenStage<HomeGivenStage> {

    @Autowired
    private HomePage homePage;
    @Autowired
    private RobotService robotService;

    @Override
    protected HomePage mainPage() {
        return homePage;
    }

    public HomeGivenStage on_home_page() {
        open(homePage);

        return self();
    }

    public HomeGivenStage see_add_robot_button() {
        assumeTakingScreenshotThat(homePage.canSeeAddRobotButton(),
            "Check if can see add robot button")
            .withFailMessage("No add robot button - try logging in")
            .isTrue();

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeGivenStage robot_with_name_$_does_not_exist(@Quoted String robotName) {
        assumeThatThrownBy(() -> robotService.findRobotByName(robotName, CURRENT_USER.getId()))
            .as("Check if robot with name \"%s\" does not exist (name should be unique among all users)", robotName)
            .isInstanceOf(RobotNotFoundException.class);

        return self();
    }

    @NestedSteps
    @ExtendedDescription(RESOLVED_IN_DATABASE)
    public HomeGivenStage robot_with_name_$_exists(@Quoted String robotName) {
        return robot_with_name_$_does_not_exist(robotName)
            .and().robot_with_name_$_is_created(robotName);
    }

    @ExtendedDescription(RESOLVED_IN_DATABASE)
    public HomeGivenStage robot_with_name_$_is_created(@Quoted String robotName) {
        return robotIsCreatedWithOwner(robotName, CURRENT_USER.getId());
    }

    @ExtendedDescription(RESOLVED_IN_DATABASE)
    public HomeGivenStage robot_$_is_owned_by(@Quoted String robotName, String ownerName) {
        return robotIsCreatedWithOwner(robotName, getCreatedUserId(ownerName));
    }

    @Hidden
    public HomeGivenStage robotIsCreatedWithOwner(String robotName, int ownerId) {
        RobotsRecord robotsRecord = new RobotsRecord();
        robotsRecord.setName(robotName);
        robotsRecord.setOwnerId(ownerId);

        assumeThatCode(() -> createEntity(RobotEntity.class, robotsRecord))
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

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeGivenStage its_owner() {
        assumeThat(robotService.getRobotOwnerIdByRobotId(getCreatedRobot().getId()))
            .as("Check if current user is the owner of the robot")
            .isEqualTo(CURRENT_USER.getId());

        return self();
    }

    public HomeGivenStage see_robot_$_as_a_card(@Quoted String robotName) {
        return press_the_refresh_robots_button()
            .and().assumeSeeRobotAsCard(robotName);
    }

    @ExtendedDescription(RESOLVED_IN_DATABASE)
    public HomeGivenStage it_is_shared_with_me() {
        return it_is_shared_with(CURRENT_USER.getUsername());
    }

    @ExtendedDescription(RESOLVED_IN_DATABASE)
    public HomeGivenStage it_is_shared_with(String... usernames) {
        RobotEntity robot = getCreatedRobot();

        assumeThat(robotService.shareRobotWithUser(robot.getId(), robot.getOwner().getId(), List.of(usernames)))
            .as("Assume that the robot is shared with me")
            .isTrue();

        return self();
    }

    public HomeGivenStage it_has_no_custom_commands() {
        RobotEntity robot = getCreatedRobot();

        assumeThat(robotService.getCustomRobotCommands(robot.getId()))
            .as("Assume robot has no custom commands")
            .isEmpty();

        return self();
    }

    public HomeGivenStage it_has_custom_commands(@Format(CustomCommandTypeArrayFormatter.class) CustomCommandType... customCommands) {
        int robotId = getCreatedRobot().getId();
        robotService.createCustomCommands(robotId, List.of(customCommands));

        assumeThat(robotService.getCustomRobotCommands(robotId))
            .as("Assume commands are created")
            .containsExactlyInAnyOrder(customCommands);

        return self();
    }

    @Hidden
    private HomeGivenStage assumeSeeRobotAsCard(String robotName) {
        assumeTakingScreenshotThat(homePage.thereIsARobotCardWithName(robotName),
            "Assume there is a card with name \"%s\"", robotName)
            .withFailMessage("No card with name \"%s\" found", robotName)
            .isTrue();

        return self();
    }

    private RobotEntity getCreatedRobot() {
        Set<RobotEntity> createdRobots = createdEntities.getInstances(RobotEntity.class);
        assumeThat(createdRobots)
            .as("Assume that only one robot has been created")
            .hasSize(1);

        return createdRobots.iterator().next();
    }
}
