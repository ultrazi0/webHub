package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Home.HomePage;
import com.nemo.testing.core.Persistence.RobotService;
import com.nemo.webHub.Decibel.RobotEntity;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class HomeThenStage extends AbstractThenStage<HomeThenStage> {

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

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage robot_is_created(String robotName) {
        assertThatCode(() -> robotService.findRobotByName(robotName, CURRENT_USER.getId()))
            .as("Check in the database if the robot is created")
            .doesNotThrowAnyException();

        return self();
    }

    public HomeThenStage see_robot_has_been_created_alert() {
        assertTakingScreenshotThat(homePage.seeSuccessAlert("New robot has been created!"),
            "Check the creation successful alert")
            .isTrue();

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
            .and().the_passwords_match()
            .and().the_owner_is_correct()
            .and().the_creation_date_is_not_blank()
            .and().the_online_status_is_correct();
    }

    public HomeThenStage the_names_match(@Hidden String robotName) {
        assertTakingScreenshotThat(homePage.getRobotNameFromInfoModal(),
            "Check if the names match")
            .isEqualTo(robotName);

        return self();
    }

    public HomeThenStage the_ids_match() {
        assertTakingScreenshotThat(homePage.getRobotIdFromInfoModal(),
            "Check if the ids match")
            .isEqualTo(getCreatedRobot().getId());

        return self();
    }

    public HomeThenStage the_passwords_match() {
        assertTakingScreenshotThat(homePage.getRobotPasswordFromInfoModal(), "Check if the passwords match")
            .isEqualTo(getCreatedRobot().getPasswordWithoutEncoding());

        return self();
    }

    public HomeThenStage the_owner_is_correct() {
        assertTakingScreenshotThat(homePage.getRobotOwnedByFromInfoModal(),
            "Check if the owner is correct")
            .isEqualTo(CURRENT_USER.getUsername());

        return self();
    }

    public HomeThenStage the_creation_date_is_not_blank() {
        assertTakingScreenshotThat(homePage.getRobotCreatedAtFromInfoModal(), "Check creation date is not blank")
            .isNotBlank();

        return self();
    }

    public HomeThenStage the_online_status_is_correct() {
        assertTakingScreenshotThat(homePage.getRobotOnlineStatusFromInfoModal(), "Check if online status is matches")
            .isEqualTo(getCreatedRobot().isOnline() ? "online" : "offline");

        return self();
    }

    public HomeThenStage see_the_deletion_successful_alert() {
        assertTakingScreenshotThat(homePage.seeSuccessAlert("Robot has been deleted"),
            "Check the deletion successful alert")
            .isTrue();

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage robot_$_does_not_exist(@Quoted String robotName) {
        assertThatCode(() -> robotService.findRobotByName(robotName, CURRENT_USER.getId()))
            .as("Assert that robot with name \"%s\" does not exist", robotName)
            .isInstanceOf(RuntimeException.class);

        return self();
    }

    public HomeThenStage see_no_robot_named(@Quoted String robotName) {
        assertTakingScreenshotThat(homePage.thereIsNoRobotCardWithName(robotName),
            "Assert there is no robot named \"%s\"", robotName)
            .isTrue();

        return self();
    }

    public HomeThenStage see_the_edit_successful_alert() {
        assertTakingScreenshotThat(homePage.seeSuccessAlert("Robot has been updated!"),
            "Check the update successful alert")
            .isTrue();

        return self();
    }

    public HomeThenStage see_the_edit_button_on(@Quoted String robotName) {
        assertTakingScreenshotThat(homePage.editButtonOnAnExistingCardIsDisplayed(robotName),
            "Check the edit button is displayed on \"%s\"", robotName)
            .isTrue();

        return self();
    }

    private RobotEntity getCreatedRobot() {
        Set<RobotEntity> createdRobots = createdEntities.getInstances(RobotEntity.class);
        assertThat(createdRobots)
            .as("Assert that only one robot has been created in this test")
            .hasSize(1);

        return createdRobots.iterator().next();
    }
}
