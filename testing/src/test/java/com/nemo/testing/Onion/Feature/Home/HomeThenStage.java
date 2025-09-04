package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Home.HomePage;
import com.nemo.testing.Onion.Model.User.UserPage;
import com.nemo.testing.core.Formatters.CustomCommandTypeArrayFormatter;
import com.nemo.testing.core.Formatters.CustomCommandTypeFormatter;
import com.nemo.testing.core.Persistence.RobotService;
import com.nemo.webHub.Commands.CommandType;
import com.nemo.webHub.Commands.CustomCommandType;
import com.nemo.webHub.Commands.StandardCommandType;
import com.nemo.webHub.Decibel.RobotEntity;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage robot_$_exists(String robotName, @Hidden String ownerUsername) {
        assertThatCode(() -> robotService.findRobotByName(robotName, getCreatedUserId(ownerUsername)))
            .as("Check in the database if the robot exists")
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
    public HomeThenStage all_the_data_is_correct(@Hidden String robotName, @Hidden @Nullable String ownerUsername) {
        return the_names_match(robotName)
            .and().the_ids_match()
            .and().the_passwords_match()
            .and().the_owner_is(Objects.requireNonNullElseGet(ownerUsername, () -> CURRENT_USER.getUsername()))
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

    public HomeThenStage the_owner_is(String username) {
        assertTakingScreenshotThat(homePage.getRobotOwnedByFromInfoModal(),
            "Check if the owner is correct")
            .isEqualTo(username);

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

    public HomeThenStage do_not_see_the_edit_button_on(@Quoted String robotName) {
        assertTakingScreenshotThat(homePage.editButtonOnAnExistingCardIsNotDisplayed(robotName),
            "Check the edit button is NOT displayed on \"%s\"", robotName)
            .isTrue();

        return self();
    }

    public HomeThenStage see_$_in_the_list(String username) {

        assertTakingScreenshotThat(
            homePage.getSharedUserLinkUsernameInInfoModal(0),
            "Check username in the link is \"%s\"",
            username
        ).isEqualTo(username);

        assertTakingScreenshotThat(
            homePage.getSharedUserLinkHrefInInfoModal(0),
            "Check the link is pointing to the correct user"
        ).isEqualTo(fullUrlOf(UserPage.getUri(getCreatedUserId(username))));

        return self();
    }

    public HomeThenStage the_row_for_$_disappears(String username) {
        assertTakingScreenshotThat(homePage.sharedUserRowHasDisappearedFor(username), "Assert that the user's row has disappeared")
            .isTrue();

        return self();
    }

    public HomeThenStage there_are_$_users_with_whom_this_robot_is_shared(int numberOfUsers) {
        assertTakingScreenshotThat(
            homePage.getAmountOfUserRows(),
            "Assert that the robot is shared with %d users",
            numberOfUsers
        ).isEqualTo(numberOfUsers);

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage the_robot_is_shared_with(String username) {
        assertThat(robotService.getSharedUsersUsernames(getCreatedRobot()))
            .as("Assert that the robot is shared with \"%s\"", username)
            .contains(username);

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage the_robot_is_not_shared_with(String username) {
        assertThat(robotService.getSharedUsersUsernames(getCreatedRobot()))
            .as("Assert that the robot is shared with \"%s\"", username)
            .doesNotContain(username);

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage the_robot_is_not_shared_with_me() {
        assertThat(robotService.getSharedUsersUsernames(getCreatedRobot()))
            .as("Assert that the robot is not shared with the current user")
            .doesNotContain(CURRENT_USER.getUsername());

        return self();
    }

    public HomeThenStage do_not_see_a_button_to_open_the_shared_with_side_panel() {
        assertTakingScreenshotThat(
            homePage.hideShowSharedWithButtonIsDisplayedInInfoModal(),
            "Assert that the show shared with button is not displayed"
        ).isFalse();

        return self();
    }

    public HomeThenStage see_a_link_to_$_as_its_owner(String username) {
        RobotEntity robot = getCreatedRobot();

        assertTakingScreenshotThat(
            homePage.getTheNameOfTheRobotsOwner(robot.getName()),
            "The robot's owner name is \"%s\"",
            username
        ).isEqualTo(username);

        assertTakingScreenshotThat(
            homePage.getTheRobotsOwnerHref(robot.getName()),
            "The link is pointing to the correct user"
        ).isEqualTo(fullUrlOf(UserPage.getUri(getCreatedUserId(username))));

        return self();
    }

    public HomeThenStage see_the_list_of_standard_commands() {
        String[] expectedCommands = Arrays.stream(StandardCommandType.values())
            .map(CommandType::getCommandType)
            .toArray(String[]::new);

        assertTakingScreenshotThat(
            homePage.getCommandsInEditModal(),
            "Assert that only standard commands are present")
            .containsExactlyInAnyOrder(expectedCommands);

        for (StandardCommandType standardCommand : StandardCommandType.values()) {
            see_the_correct_command_details(standardCommand);
        }

        return self();
    }

    public HomeThenStage see_standard_commands_as_well_as_the_custom_ones(@Hidden CustomCommandType... customCommands) {
        String[] expectedCommands = Stream.concat(
            Arrays.stream(StandardCommandType.values()), Arrays.stream(customCommands)
            ).map(CommandType::getCommandType)
            .toArray(String[]::new);

        currentStep.setExtendedDescription("Expected custom commands: " + Arrays.stream(customCommands)
            .map(CustomCommandTypeFormatter::formatCustomCommandType)
            .collect(Collectors.joining(", ")));

        assertTakingScreenshotThat(
            homePage.getCommandsInEditModal(),
            "Assert that only standard commands are present")
            .containsExactlyInAnyOrder(expectedCommands);

        for (StandardCommandType standardCommand : StandardCommandType.values()) {
            see_the_correct_command_details(standardCommand);
        }

        for (CustomCommandType customCommand : customCommands) {
            see_the_correct_command_details(customCommand);
        }

        return self();
    }

    public HomeThenStage see_the_correct_command_details(CommandType customCommand) {
        homePage.openCommandForm(customCommand.getCommandType());

        assertTakingScreenshotThat(
            homePage.commandNameInputFieldIsVisible(),
            "Assert command name input field is visible"
        ).isTrue();

        assertTakingScreenshotThat(
            homePage.getTextFromTheCommandInputField(),
            "Assert command name input field has correct value"
        ).isEqualTo(customCommand.getCommandType());

        assertTakingScreenshotThat(
            homePage.getKeysFromTheCommandForm(),
            "Assert command keys are correct"
        ).containsExactlyInAnyOrder(customCommand.getKeys());

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage the_robot_has_the_following_custom_commands(@Format(CustomCommandTypeArrayFormatter.class) CustomCommandType... customCommands) {
        RobotEntity robot = getCreatedRobot();

        assertThat(robotService.getCustomRobotCommands(robot.getId()))
            .as("Assert exactly %s custom commands have been created", customCommands.length)
            .hasSize(customCommands.length)
            .as("Assert the robot has exactly the provided commands")
            .containsExactlyInAnyOrder(customCommands);

        return self();
    }

    public HomeThenStage cannot_press_the_save_button_in_the_edit_modal() {

        assertTakingScreenshotThat(
            homePage.saveButtonInEditModalIsDisabled(),
            "Assert that the save button is disabled"
        ).isTrue();

        return self();
    }

    public HomeThenStage the_reason_should_be(@Quoted String reason) {

        assertTakingScreenshotThat(
            homePage.getCommandInputFieldErrorMessage(),
            "Assert that the command input field error message is correct"
        ).isEqualTo(reason);

        return self();
    }

    public HomeThenStage should_not_see_the_delete_button_for_standard_commands() {

        for (CommandType standardCommand : StandardCommandType.values()) {
            assertTakingScreenshotThat(
                homePage.removeButtonIsHiddenFor(standardCommand.getCommandType()),
                "Assert that the delete button is hidden for \"%s\"",
                standardCommand.getCommandType()
            ).isTrue();
        }

        return self();
    }

    public HomeThenStage the_command_input_field_is_disabled() {

        assertTakingScreenshotThat(
            homePage.enterCommandNameIsDisabled(),
            "Assert that the command input field is disabled"
        ).isTrue();

        return self();
    }

    public HomeThenStage the_add_key_button_is_not_displayed() {

        assertTakingScreenshotThat(
            homePage.addKeyButtonIsHidden(),
            "Assert that the add key button is not displayed"
        ).isTrue();

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
