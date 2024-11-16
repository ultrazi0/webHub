package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.core.APrivateInvestigatorTest;
import com.nemo.webHub.Commands.CommandType;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@APrivateInvestigatorTest
@SuppressWarnings("ResultOfMethodCallIgnored")
class HomeTests extends SpringScenarioTest<HomeGivenStage, HomeWhenStage, HomeThenStage> {

    static String DEFAULT_ROBOT_NAME = "myTestRobot";

    @Test
    void if_logged_in_I_should_be_able_to_view_the_list_of_all_commands() {

        given()
            .I_am().a().test_user();

        when()
            .I().send_request_to().get_all_commands_endpoint();

        then()
            .response_is_correct()
            .and().I().get_all_commands();

    }

    @ParameterizedTest
    @EnumSource(CommandType.class)
    void I_should_be_able_to_view_the_values_of_all_commands(CommandType commandType) {
        String command = commandType.name();

        given()
            .I_am().a().test_user()
            .and().I().request_values_of_command(command);

        when()
            .I().send_request_to().command_values_endpoint();

        then()
            .response_is_correct()
            .and().I().get_values_for_command(command);
    }

    @Test
    void provided_I_have_a_robot_I_should_be_able_to_get_its_details() {

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().I().request_it();

        when()
            .I().send_request_to().get_robot_endpoint();

        then()
            .response_is_correct()
            .and().I().get_the_correct_robot();
    }

    @Test
    void insertTest() {

        given()
            .I_am().a().test_user()
            .and().robot_$_does_not_exist(DEFAULT_ROBOT_NAME)
            .and().I().set_its_name_to(DEFAULT_ROBOT_NAME);

        when()
            .I().send_request_to().insert_robot_endpoint();

        then()
            .robot_$_is_created(DEFAULT_ROBOT_NAME)
            .and().response_is_correct(201)
            .and().the_name_of_the_returned_robot_is(DEFAULT_ROBOT_NAME);

    }
}
