package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.core.OnionTest;
import com.nemo.testing.core.Tags.ShareRobots;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

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

}
