package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.core.APrivateInvestigatorTest;
import com.nemo.webHub.Commands.CommandType;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@APrivateInvestigatorTest
public class HomeTests extends SpringScenarioTest<HomeGivenStage, HomeWhenStage, HomeThenStage> {

    String DEFAULT_ROBOT_NAME = "myTestRobot";

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
}
