package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.core.API.WithErrorMessages;
import com.nemo.testing.core.APrivateInvestigatorTest;
import com.nemo.testing.core.Tags.CustomCommandsOdyssey;
import com.nemo.testing.core.Tags.WH;
import com.nemo.webHub.Commands.CustomCommandType;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

@APrivateInvestigatorTest
class HomeTests extends SpringScenarioTest<HomeGivenStage, HomeWhenStage, HomeThenStage>
    implements WithErrorMessages {

    static String DEFAULT_ROBOT_NAME = "myTestRobot";

    @Test
    @CustomCommandsOdyssey
    void if_robot_exists_it_should_have_standard_commands() {

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().I().request_it();

        when()
            .I().send_request_to().the().get_robot_commands_endpoint();

        then()
            .response_is_correct()
            .and().I().get_a_list_of_only_standard_commands();

    }

    @Test
    @CustomCommandsOdyssey
    void provided_robot_has_custom_commands_I_should_be_able_to_get_them() {
        CustomCommandType[] customCommands = {
            new CustomCommandType("CUSTOM_COMMAND0", new String[]{}),
            new CustomCommandType("CUSTOM_COMMAND1", new String[]{"Key1"}),
            new CustomCommandType("CUSTOM_COMMAND2", new String[]{"Key1", "Key2"})
        };

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().it_has_custom_commands(customCommands)
            .and().I().request_it();

        when()
            .I().send_request_to().the().get_robot_endpoint();

        then()
            .response_is_correct()
            .and().I().get_the_correct_robot()
            .and().the_retrieved_robot_has_standard_commands_as_well_as_custom_ones(customCommands);

    }

    @Test
    @CustomCommandsOdyssey
    void if_robot_exists_I_should_be_able_to_insert_custom_commands() {

        CustomCommandType customCommand = new CustomCommandType("CUSTOM_COMMAND", new String[]{"Key1", "Key2"});

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().I().create_the_$_command(customCommand);

        when()
            .I().send_request_to().the().insert_command_endpoint();

        then()
            .response_is_correct()
            .and().the_robot_has_correct_custom_commands(customCommand);

    }

    @Test
    @CustomCommandsOdyssey
    void if_robot_has_custom_commands_I_should_be_able_to_delete_them() {
        CustomCommandType command1 = new CustomCommandType("CUSTOM_COMMAND1", new String[]{"Key1"});
        CustomCommandType command2 = new CustomCommandType("CUSTOM_COMMAND2", new String[]{"Key1", "Key2"});

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().it_has_custom_commands(command1, command2)
            .and().I().request_to_delete_the_$_command(command1.getCommandType());

        when()
            .I().send_request_to().the().delete_command_endpoint();

        then()
            .the().response_is_correct(204)
            .and().the_robot_has_correct_custom_commands(command2);
    }

    @WH("2")
    @Test
    void provided_I_have_a_robot_I_should_be_able_to_get_its_details() {

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().I().request_it();

        when()
            .I().send_request_to().the().get_robot_endpoint();

        then()
            .response_is_correct()
            .and().I().get_the_correct_robot();
    }

    @WH("3")
    @Test
    void provided_robot_with_this_name_does_not_exist_I_am_able_to_create_a_new_one() {

        given()
            .I_am().a().test_user()
            .and().robot_$_does_not_exist(DEFAULT_ROBOT_NAME)
            .and().I().set_new_robots_name_to(DEFAULT_ROBOT_NAME);

        when()
            .I().send_request_to().the().insert_robot_endpoint();

        then()
            .robot_$_is_created(DEFAULT_ROBOT_NAME)
            .and().response_is_correct(201)
            .and().the_name_of_the_returned_robot_is(DEFAULT_ROBOT_NAME);

    }

    @Test
    void provided_robot_with_this_name_already_exists_I_cannot_create_a_new_one() {

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().I().set_new_robots_name_to(DEFAULT_ROBOT_NAME);

        when()
            .I().send_request_to().the().insert_robot_endpoint();

        then()
            .and().response_is_correct(409)
            .and().I().get_an_error(NAME_ALREADY_TAKEN);

    }

    @Test
    void provided_robot_exists_I_can_edit_its_name() {
        final String originalRobotName = "originalRobotName";
        final String editedRobotName = "editedRobotName";

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(originalRobotName)
            .and().I().change_its_name_to(editedRobotName);

        when()
            .I().send_request_to().the().edit_robot_endpoint();

        then()
            .response_is_correct(201)
            .and().its_name_is_changed_from_$_to(originalRobotName, editedRobotName)
            .and().the_name_of_the_returned_robot_is(editedRobotName);

    }

    @Test
    @CustomCommandsOdyssey
    void provided_robot_exists_I_can_edit_its_commands() {
        final CustomCommandType[] originalCustomCommands = {
            new CustomCommandType("CUSTOM_COMMAND0", new String[]{}),
            new CustomCommandType("CUSTOM_COMMAND1", new String[]{"Key1"})
        };
        final CustomCommandType[] editedCustomCommands = {
            new CustomCommandType("CUSTOM_COMMAND0", new String[]{"Key0"}),
            new CustomCommandType("CUSTOM_COMMAND2", new String[]{"Key1", "Key2"})
        };

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().it_has_custom_commands(originalCustomCommands)
            .and().I().request_to_change_its_commands_to(editedCustomCommands);

        when()
            .I().send_request_to().the().edit_robot_endpoint();

        then()
            .response_is_correct(201)
            .and().the_robot_has_correct_custom_commands(editedCustomCommands)
            .and().the_name_of_the_returned_robot_is(DEFAULT_ROBOT_NAME);

    }

    @Test
    void provided_robot_exists_I_can_delete_it() {

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().I().want_to_delete_it();

        when()
            .I().send_request_to().the().delete_robot_endpoint();

        then()
            .robot_with_name_$_does_not_exit(DEFAULT_ROBOT_NAME)
            .and().response_is_correct(204);

    }

    @MethodSource
    @ParameterizedTest
    void provided_I_have_robots_I_am_able_to_get_them(List<String> robotNames) {

        given()
            .I_am().a().test_user()
            .and().I().have_robots(robotNames);

        when()
            .I().send_request_to().the().get_user_robots_endpoint();

        then()
            .response_is_correct()
            .and().I().get_all_my_robots(robotNames);

    }

    @Test
    void if_robot_exists_it_is_possible_to_share_it_with_another_user() {
        final String otherUser1 = "otherUser1";
        final String otherUser2 = "otherUser2";

        given()
            .I_am().a().test_user()
            .and().user_$_exists(otherUser1, otherUser1)
            .and().user_$_exists(otherUser2, otherUser2)
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().I().want_to_share_it_with(otherUser1, otherUser2);

        when()
            .I().send_request_to().the().share_robot_endpoint();

        then()
            .the().response_is_correct()
            .and().the_robot_is_shared_with(otherUser1, otherUser2);

    }

    private static Stream<List<String>> provided_I_have_robots_I_am_able_to_get_them() {
        return Stream.of(
            List.of(),
            List.of(DEFAULT_ROBOT_NAME),
            List.of(DEFAULT_ROBOT_NAME+"1", DEFAULT_ROBOT_NAME+"2", DEFAULT_ROBOT_NAME+"3")
        );
    }
}
