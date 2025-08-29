package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Home.HomePage;
import com.nemo.webHub.Decibel.RobotEntity;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class HomeWhenStage extends AbstractWhenStage<HomeWhenStage> {

    @Autowired
    private HomePage homePage;

    @Override
    protected AbstractPage mainPage() {
        return homePage;
    }

    @NestedSteps
    public HomeWhenStage add_a_new_robot(@Quoted String robotName) {
        return I().press_add_robot_button()
            .and().in_the_modal_I_enter_robot_name(robotName)
            .and().in_the_modal_I_press_add_robot_button()
            .and().I().registerEntity(RobotEntity.class, robotName);
    }

    public HomeWhenStage press_add_robot_button() {
        homePage.pressAddRobotButton();

        return self();
    }

    public HomeWhenStage in_the_modal_I_enter_robot_name(@Quoted String robotName) {
        homePage.enterRobotNameInTheModal(robotName);

        return self();
    }

    public HomeWhenStage in_the_modal_I_press_add_robot_button() {
        homePage.pressAddRobotButtonInTheModal();

        return self();
    }

    @ExtendedDescription("Needed to make sure that the card is displayed")
    public HomeWhenStage press_the_refresh_robots_button() {
        homePage.pressRefreshRobotsButton();

        return self();
    }

    public HomeWhenStage press_on_the_info_button_on_the_robot(@Quoted String robotName) {
        homePage.clickOnInfoButton(robotName);

        return self();
    }

    @NestedSteps
    public HomeWhenStage delete_the_robot(@Quoted String defaultRobotName) {
        return I().press_the_delete_button_on(defaultRobotName)
            .and().I_confirm_the_deletion_in_the_modal();
    }

    public HomeWhenStage press_the_delete_button_on(@Quoted String defaultRobotName) {
        homePage.clickOnDeleteButton(defaultRobotName);

        return self();
    }

    private HomeWhenStage I_confirm_the_deletion_in_the_modal() {
        homePage.pressDeleteButtonInDeleteModal();

        return self();
    }

    @NestedSteps
    public HomeWhenStage change_robot_name_from_$_to(@Quoted String oldRobotName, @Quoted String newRobotName) {
        return I().press_the_edit_button_on(oldRobotName)
            .and().I().change_robot_name_to(newRobotName);
    }

    public HomeWhenStage press_the_edit_button_on(@Quoted String oldRobotName) {
        homePage.clickOnEditButton(oldRobotName);

        return self();
    }

    public HomeWhenStage change_robot_name_to(@Quoted String newRobotName) {
        homePage.enterNewRobotNameInEditModal(newRobotName);
        homePage.pressSaveButtonInEditModal();

        return self();
    }

    public HomeWhenStage open_the_shared_with_side_panel() {
        homePage.pressHideShowSharedWithButtonInInfoModal();

        return self();
    }

    @NestedSteps
    public HomeWhenStage share_the_robot_with(String username) {
        return I().press_add_user_button()
            .and().I().enter_username_of_the_user_I_want_to_share_the_robot_with(username, 0)
            .and().I().press_share_user_button(0);
    }

    public HomeWhenStage press_add_user_button() {
        homePage.pressAddUserButtonInInfoModal();

        return self();
    }

    public HomeWhenStage enter_username_of_the_user_I_want_to_share_the_robot_with(@Hidden String username, @Hidden int rowIndex) {
        homePage.enterUsernameToShareWith(username, rowIndex);

        return self();
    }

    public HomeWhenStage press_share_user_button(@Hidden int rowIndex) {
        homePage.pressShareUserButton(rowIndex);

        return self();
    }

    public HomeWhenStage stop_sharing_the_robot_with(String username) {
        homePage.pressUnshareUserButtonFor(username);

        return self();
    }
}
