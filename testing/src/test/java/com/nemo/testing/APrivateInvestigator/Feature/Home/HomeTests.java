package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.core.API.WithErrorMessages;
import com.nemo.testing.core.APrivateInvestigatorTest;
import com.nemo.testing.core.Tags.WH;
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
    void if_robot_exists_it_should_have_standard_commands() {

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().I().request_it();

        when()
            .I().send_request_to().the_get_robot_commands_endpoint();

        then()
            .response_is_correct()
            .and().I().get_a_list_of_only_standard_commands();

    }

    @WH("2")
    @Test
    void provided_I_have_a_robot_I_should_be_able_to_get_its_details() {

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().I().request_it();

        when()
            .I().send_request_to().the_get_robot_endpoint();

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
            .I().send_request_to().the_insert_robot_endpoint();

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
            .I().send_request_to().the_insert_robot_endpoint();

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
            .I().send_request_to().the_edit_robot_endpoint();

        then()
            .response_is_correct(201)
            .and().its_name_is_changed_from_$_to(originalRobotName, editedRobotName)
            .and().the_name_of_the_returned_robot_is(editedRobotName);

    }

    @Test
    void provided_robot_exists_I_can_delete_it() {

        given()
            .I_am().a().test_user()
            .and().I().have_a_robot(DEFAULT_ROBOT_NAME)
            .and().I().want_to_delete_it();

        when()
            .I().send_request_to().the_delete_robot_endpoint();

        then()
            .robot_with_name_$_does_not_exit(DEFAULT_ROBOT_NAME)
            .and().response_is_correct(204);

    }

    @ParameterizedTest
    @MethodSource
    void provided_I_have_robots_I_am_able_to_get_them(List<String> robotNames) {

        given()
            .I_am().a().test_user()
            .and().I().have_robots(robotNames);

        when()
            .I().send_request_to().the_get_user_robots_endpoint();

        then()
            .response_is_correct()
            .and().I().get_all_my_robots(robotNames);

    }

    private static Stream<List<String>> provided_I_have_robots_I_am_able_to_get_them() {
        return Stream.of(
            List.of(),
            List.of(DEFAULT_ROBOT_NAME),
            List.of(DEFAULT_ROBOT_NAME+"1", DEFAULT_ROBOT_NAME+"2", DEFAULT_ROBOT_NAME+"3")
        );
    }
}
