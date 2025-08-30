package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.core.OnionTest;
import com.nemo.testing.core.Tags.CustomCommandsOdyssey;
import com.nemo.testing.core.Tags.ShareRobots;
import com.nemo.webHub.Commands.CustomCommandType;
import com.nemo.webHub.Commands.StandardCommandType;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@OnionTest
class HomeTests extends SpringScenarioTest<HomeGivenStage, HomeWhenStage, HomeThenStage> {

    String DEFAULT_ROBOT_NAME = "myTestRobot";

    @Test
    void if_robot_does_not_exist_I_should_be_able_to_create_a_new_one() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().I().see_add_robot_button()
            .and().robot_with_name_$_does_not_exist(DEFAULT_ROBOT_NAME);

        when()
            .I().add_a_new_robot(DEFAULT_ROBOT_NAME);

        then()
            .robot_is_created(DEFAULT_ROBOT_NAME)
            .and().I().see_robot_has_been_created_alert()
            .and().I().see_robot_$_as_a_card(DEFAULT_ROBOT_NAME);

    }

    @Test
    void given_robot_exits_then_I_should_see_its_data_in_the_info_modal() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().I().see_robot_$_as_a_card(DEFAULT_ROBOT_NAME);

        when()
            .I().press_on_the_info_button_on_the_robot(DEFAULT_ROBOT_NAME);

        then()
            .I().see_robots_info_modal()
            .and().all_the_data_is_correct(DEFAULT_ROBOT_NAME, null);

    }

    @Test
    void given_robot_exits_then_I_should_be_able_to_delete_it() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().I().see_robot_$_as_a_card(DEFAULT_ROBOT_NAME);

        when()
            .I().delete_the_robot(DEFAULT_ROBOT_NAME);

        then()
            .I().see_the_deletion_successful_alert()
            .and().robot_$_does_not_exist(DEFAULT_ROBOT_NAME)
            .and().I().see_no_robot_named(DEFAULT_ROBOT_NAME);

    }

    @Test
    void given_robot_exits_then_I_should_be_able_to_edit_it() {

        String oldRobotName = "oldRobotName";
        String newRobotName = "newRobotName";

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(oldRobotName)
            .and().I().can_edit_robot(oldRobotName);

        when()
            .I().change_robot_name_from_$_to(oldRobotName, newRobotName);

        then()
            .I().see_the_edit_successful_alert()
            .and().I().see_robot_$_as_a_card(newRobotName);
    }

    @Test
    void given_I_am_the_robots_owner_then_I_should_see_the_edit_button() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().I_am().its_owner();

        when()
            .I().press_the_refresh_robots_button();

        then()
            .I().see_robot_$_as_a_card(DEFAULT_ROBOT_NAME)
            .and().I().see_the_edit_button_on(DEFAULT_ROBOT_NAME);
    }

    @Test
    @ShareRobots
    void given_the_robot_is_shared_with_me_I_should_not_see_the_edit_button() {

        final String robotOwnerUsername = "robotOwner";

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().user_$_exists(robotOwnerUsername)
            .and().robot_$_is_owned_by(DEFAULT_ROBOT_NAME, robotOwnerUsername)
            .and().it_is_shared_with_me();

        when()
            .I().press_the_refresh_robots_button();

        then()
            .I().see_robot_$_as_a_card(DEFAULT_ROBOT_NAME)
            .and().I().do_not_see_the_edit_button_on(DEFAULT_ROBOT_NAME);

    }

    @Test
    @ShareRobots
    void given_the_robot_is_shared_with_me_I_should_see_its_data_in_the_info_modal() {

        final String robotOwnerUsername = "robotOwner";

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().user_$_exists(robotOwnerUsername)
            .and().robot_$_is_owned_by(DEFAULT_ROBOT_NAME, robotOwnerUsername)
            .and().it_is_shared_with_me();

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_on_the_info_button_on_the_robot(DEFAULT_ROBOT_NAME);

        then()
            .I().see_robots_info_modal()
            .and().all_the_data_is_correct(DEFAULT_ROBOT_NAME, robotOwnerUsername);

    }

    @Test
    @ShareRobots
    void given_I_am_the_owner_then_I_should_be_able_to_share_my_robot() {

        final String otherUser = "otherUser";

        given()
            .I_am().a().test_user()
            .and().user_$_exists(otherUser)
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().I_am().its_owner();

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_on_the_info_button_on_the_robot(DEFAULT_ROBOT_NAME)
            .and().I().open_the_shared_with_side_panel()
            .and().I().share_the_robot_with(otherUser);

        then()
            .see_$_in_the_list(otherUser)
            .and().the_robot_is_shared_with(otherUser);

    }

    @Test
    @ShareRobots
    void given_I_am_the_owner_then_I_should_be_able_to_stop_sharing_my_robot() {

        final String otherUser = "otherUser";

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().user_$_exists(otherUser)
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().I_am().its_owner()
            .and().it_is_shared_with(otherUser);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_on_the_info_button_on_the_robot(DEFAULT_ROBOT_NAME)
            .and().I().open_the_shared_with_side_panel()
            .and().I().stop_sharing_the_robot_with(otherUser);

        then()
            .the_row_for_$_disappears(otherUser)
            .and().there_are_$_users_with_whom_this_robot_is_shared(0)
            .and().the_robot_is_not_shared_with(otherUser);

    }

    @Test
    @ShareRobots
    void given_I_am_the_owner_then_I_should_be_able_to_see_with_whom_my_robot_is_shared() {

        final String otherUser = "otherUser";

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().user_$_exists(otherUser)
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().I_am().its_owner()
            .and().it_is_shared_with(otherUser);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_on_the_info_button_on_the_robot(DEFAULT_ROBOT_NAME)
            .and().I().open_the_shared_with_side_panel();

        then()
            .I().see_$_in_the_list(otherUser);

    }

    @Test
    @ShareRobots
    void given_the_robot_is_shared_with_me_I_should_not_be_able_to_see_the_show_shared_button() {

        final String robotOwner = "robotOwner";

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().user_$_exists(robotOwner)
            .and().robot_$_is_owned_by(DEFAULT_ROBOT_NAME, robotOwner)
            .and().it_is_shared_with_me();

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_on_the_info_button_on_the_robot(DEFAULT_ROBOT_NAME);

        then()
            .I().see_robots_info_modal()
            .and().I().do_not_see_a_button_to_open_the_shared_with_side_panel();

    }

    @Test
    @ShareRobots
    void given_the_robot_is_shared_with_me_I_should_be_able_to_see_its_owner() {

        final String robotOwner = "robotOwner";

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().user_$_exists(robotOwner)
            .and().robot_$_is_owned_by(DEFAULT_ROBOT_NAME, robotOwner)
            .and().it_is_shared_with_me();

        when()
            .I().press_the_refresh_robots_button();

        then()
            .I().see_robot_$_as_a_card(DEFAULT_ROBOT_NAME)
            .and().I().see_a_link_to_$_as_its_owner(robotOwner);

    }

    @Test
    @ShareRobots
    void given_the_robot_is_shared_with_me_I_should_be_able_to_unshare_it_myself() {

        final String robotOwner = "robotOwner";

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().user_$_exists(robotOwner)
            .and().robot_$_is_owned_by(DEFAULT_ROBOT_NAME, robotOwner)
            .and().it_is_shared_with_me();

        when()
            .I().press_the_refresh_robots_button()
            .and().I().delete_the_robot(DEFAULT_ROBOT_NAME);

        then()
            .I().see_the_deletion_successful_alert()
            .and().robot_$_exists(DEFAULT_ROBOT_NAME, robotOwner)
            .and().the_robot_is_not_shared_with_me()
            .and().I().see_no_robot_named(DEFAULT_ROBOT_NAME);

    }

    @Test
    @CustomCommandsOdyssey
    void given_robot_exists_I_should_see_the_list_of_commands() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().it_has_no_custom_commands();

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME);

        then()
            .I().see_the_list_of_standard_commands();

    }

    @Test
    @CustomCommandsOdyssey
    void given_robot_has_custom_commands_they_are_visible_in_the_edit_modal() {

        CustomCommandType[] customCommands = {
            new CustomCommandType("CUSTOM_COMMAND0", new String[]{"Key0"}),
            new CustomCommandType("CUSTOM_COMMAND1", new String[]{"Key1"}),
            new CustomCommandType("CUSTOM_COMMAND2", new String[]{"Key1", "Key2"})
        };

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().it_has_custom_commands(customCommands);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME);

        then()
            .I().see_standard_commands_as_well_as_the_custom_ones(customCommands);
    }

    @Test
    @CustomCommandsOdyssey
    void given_robot_exists_I_should_be_able_to_add_a_custom_command() {

        CustomCommandType customCommand = new CustomCommandType("CUSTOM_COMMAND", new String[]{"Key1", "Key2"});

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().it_has_no_custom_commands();

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME)
            .and().add_$_as_a_custom_command(customCommand)
            .and().I().press_save_button_in_edit_modal();

        then()
            .I().see_the_edit_successful_alert()
            .and().the_robot_has_the_following_custom_commands(customCommand);

    }

    @Test
    @CustomCommandsOdyssey
    void given_robot_has_custom_commands_I_should_be_able_to_edit_a_custom_command() {

        CustomCommandType originalCustomCommand = new CustomCommandType("CUSTOM_COMMAND", new String[]{"Key1", "Key2"});
        CustomCommandType newCustomCommand = new CustomCommandType("CUSTOM_COMMAND1", new String[]{"Key1", "Key3", "Key4"});

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().it_has_custom_commands(originalCustomCommand);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME)
            .and().I().select_the_$_command(originalCustomCommand.getCommandType())
            .and().I().enter_the_command_name_as(newCustomCommand.getCommandType())
            .and().I().rename_the_$_key_to(originalCustomCommand.getKeys()[1], newCustomCommand.getKeys()[1])
            .and().I().add_$_as_command_keys(newCustomCommand.getKeys()[2])
            .and().I().press_save_button_in_edit_modal();

        then()
            .I().see_the_edit_successful_alert()
            .and().the_robot_has_the_following_custom_commands(newCustomCommand);

    }

    @Test
    @CustomCommandsOdyssey
    void given_robot_has_a_custom_command_with_keys_I_should_be_able_to_remove_some_of_them() {

        CustomCommandType originalCustomCommand = new CustomCommandType("CUSTOM_COMMAND", new String[]{"Key1", "Key2", "Key3", "Key4"});
        CustomCommandType newCustomCommand = new CustomCommandType(originalCustomCommand.getCommandType(), new String[]{"Key1", "Key3"});

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().it_has_custom_commands(originalCustomCommand);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME)
            .and().I().select_the_$_command(originalCustomCommand.getCommandType())
            .and().I().remove_the_$_key(originalCustomCommand.getKeys()[3])
            .and().I().remove_the_$_key(originalCustomCommand.getKeys()[1])
            .and().I().press_save_button_in_edit_modal();

        then()
            .I().see_the_edit_successful_alert()
            .and().the_robot_has_the_following_custom_commands(newCustomCommand);

    }

    @Test
    @CustomCommandsOdyssey
    void given_robot_has_custom_commands_I_should_be_able_to_delete_some_of_them() {

        CustomCommandType[] customCommands = {
            new CustomCommandType("CUSTOM_COMMAND0", new String[]{"Key0"}),
            new CustomCommandType("CUSTOM_COMMAND1", new String[]{"Key1"}),
            new CustomCommandType("CUSTOM_COMMAND2", new String[]{"Key1", "Key2"})
        };

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().it_has_custom_commands(customCommands);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME)
            .and().delete_the_$_command(customCommands[1].getCommandType())
            .and().delete_the_$_command(customCommands[0].getCommandType())
            .and().I().press_save_button_in_edit_modal();

        then()
            .I().see_the_edit_successful_alert()
            .and().the_robot_has_the_following_custom_commands(customCommands[2]);

    }

    @Test
    @CustomCommandsOdyssey
    void given_robot_exists_I_should_not_be_able_to_add_commands_with_the_same_name() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME)
            .and().I().press_the_add_a_custom_command_button()
            .and().I().enter_the_command_name_as(StandardCommandType.MOVE.getCommandType());

        then()
            .I().cannot_press_the_save_button_in_the_edit_modal()
            .and().I().the_reason_should_be("Commands must be unique");

    }

    @Test
    @CustomCommandsOdyssey
    void given_robot_exists_I_should_not_be_able_to_add_commands_with_blank_name() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME)
            .and().I().press_the_add_a_custom_command_button()
            .and().I().enter_the_command_name_as("  ");

        then()
            .I().cannot_press_the_save_button_in_the_edit_modal()
            .and().I().the_reason_should_be("Command must not be blank");

    }

    @Test
    @CustomCommandsOdyssey
    void given_robot_exists_I_should_not_be_able_to_delete_standard_commands() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME);

        then()
            .I().should_not_see_the_delete_button_for_standard_commands();

    }

    @ParameterizedTest
    @CustomCommandsOdyssey
    @EnumSource(StandardCommandType.class)
    void given_robot_exists_I_should_not_be_able_to_modify_standard_commands(StandardCommandType commandType) {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME)
            .and().I().select_the_$_command(commandType.getCommandType());

        then()
            .the_command_input_field_is_disabled()
            .and().the_add_key_button_is_not_displayed();

    }

    @Test
    @CustomCommandsOdyssey
    void given_robot_exists_I_should_not_be_able_to_add_a_command_with_not_unique_keys() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME);

        when()
            .I().press_the_refresh_robots_button()
            .and().I().press_the_edit_button_on(DEFAULT_ROBOT_NAME)
            .and().I().press_the_add_a_custom_command_button()
            .and().I().enter_the_command_name_as("CUSTOM_COMMAND")
            .and().I().add_$_as_command_keys("Key1", "Key1");

        then()
            .I().cannot_press_the_save_button_in_the_edit_modal();

    }

}
