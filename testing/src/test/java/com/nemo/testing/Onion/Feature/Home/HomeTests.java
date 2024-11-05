package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.core.OnionTest;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

@OnionTest
@SuppressWarnings("ResultOfMethodCallIgnored")
class HomeTests extends SpringScenarioTest<HomeGivenStage, HomeWhenStage, HomeThenStage> {

    String DEFAULT_ROBOT_NAME = "myTestRobot";

    @Test
    void testAddRobot() {

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
    void testRobotInfoButton() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_home_page()
            .and().robot_with_name_$_exists(DEFAULT_ROBOT_NAME)
            .and().I().see_robot_$_as_a_card(DEFAULT_ROBOT_NAME);

        when()
            .I().press_on_the_info_button_on_the_robot(DEFAULT_ROBOT_NAME);

        then()
            .I().see_robots_info_modal()
            .and().all_the_data_is_correct(DEFAULT_ROBOT_NAME);

    }

    @Test
    void testRobotDeleteButton() {

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
    void testRobotEditButton() {

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
    void robotEditButtonShouldBeVisibleWhenOwnerTest() {

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

}
