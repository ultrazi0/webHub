package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.core.OnionTest;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

@OnionTest
@SuppressWarnings("ResultOfMethodCallIgnored")
public class HomeTests extends SpringScenarioTest<HomeGivenStage, HomeWhenStage, HomeThenStage> {

    String DEFAULT_ROBOT_NAME = "myTestRobot";

    @Test
    void testAddRobot() {

        given()
            .I_am().test_user()
            .and().I_am().on_home_page()
            .and().I().see_add_robot_button()
            .and().robot_with_name_$_does_not_exist(DEFAULT_ROBOT_NAME);

        when()
            .I().add_a_new_robot(DEFAULT_ROBOT_NAME);

        then()
            .robot_is_created(DEFAULT_ROBOT_NAME).
            and().I().see_robot_$_as_a_card(DEFAULT_ROBOT_NAME);

    }

    void testRobotInfoButton() {

        given()
            .I_am().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME);

    }

}
