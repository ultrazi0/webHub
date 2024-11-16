package com.nemo.testing.APrivateInvestigator.Feature.User;

import com.nemo.testing.core.APrivateInvestigatorTest;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

@APrivateInvestigatorTest
@SuppressWarnings("ResultOfMethodCallIgnored")
class UserTests extends SpringScenarioTest<UserGivenStage, UserWhenStage, UserThenStage> {

    private static final String oldUsername = "myOldTestUsername";
    private static final String newUsername = "myNewTestUsername";
    private static final String oldPassword = "myOldTestPassword";
    private static final String newPassword = "myNewTestPassword";

    @Test
    void if_logged_in_I_can_view_my_user_details() {

        given()
            .user_$_exists(oldUsername, oldPassword)
            .and().I_am().logged_in_as_$_with_password(oldUsername, oldPassword);

        when()
            .I().send_request_to().get_user_endpoint();

        then()
            .response_is_correct(oldUsername);
    }

    @Test
    void if_logged_in_I_can_change_my_username() {

        given()
            .user_$_exists(oldUsername, oldPassword)
            .and().I_am().logged_in_as_$_with_password(oldUsername, oldPassword)
            .and().I().change_username_to(newUsername, oldPassword);

        when()
            .I().send_request_to().edit_user_endpoint();

        then()
            .username_is_updated_to(newUsername)
            .and().response_is_correct(newUsername);

    }

    @Test
    void if_logged_in_I_can_change_my_password() {

        given()
            .user_$_exists(oldUsername, oldPassword)
            .and().I_am().logged_in_as_$_with_password(oldUsername, oldPassword)
            .and().I().change_password_to(newPassword, oldPassword);

        when()
            .I().send_request_to().edit_user_endpoint();

        then()
            .password_is_changed()
            .and().response_is_correct();

    }

    @Test
    void if_logged_in_I_can_delete_my_user() {

        given()
            .user_$_exists(oldUsername, oldPassword)
            .and().I_am().logged_in_as_$_with_password(oldUsername, oldPassword);

        when()
            .I().send_request_to().delete_user_endpoint();

        then()
            .user_$_is_deleted(oldUsername)
            .and().response_is_correct(204)
            .and().I_am().logged_out();

    }

}
