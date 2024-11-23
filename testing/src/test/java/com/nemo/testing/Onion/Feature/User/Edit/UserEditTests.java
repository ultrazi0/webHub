package com.nemo.testing.Onion.Feature.User.Edit;

import com.nemo.testing.core.OnionTest;
import com.nemo.testing.core.WithDefaultCredentials;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

@OnionTest
@SuppressWarnings("ResultOfMethodCallIgnored")
class UserEditTests extends SpringScenarioTest<UserEditPageGivenStage, UserEditPageWhenStage, UserEditPageThenStage>
    implements WithDefaultCredentials {

    private static final String oldUsername = "myOldTestUsername";
    private static final String newUsername = "myNewTestUsername";
    private static final String oldPassword = "myOldTestPassword";
    private static final String newPassword = "myNewTestPassword";

    @Test
    void my_username_is_prefilled() {
        given()
            .I_am().a().test_user()
            .and().I_am().on_user_edit_page();

        then()
            .my_username_is_prefilled();

    }

    @Test
    void I_can_edit_my_username() {

        given()
            .I_am().logged_in_as_a_new_user(oldUsername, DEFAULT_PASSWORD)
            .and().I_am().on_user_edit_page();

        when()
            .I().change_my_username_to(newUsername, DEFAULT_PASSWORD);

        then()
            .I().see_my_profile_page()
            .and().username_is_changed_to(newUsername);

    }

    @Test
    void I_can_edit_my_password() {

        given()
            .I_am().logged_in_as_a_new_user(DEFAULT_USERNAME, oldPassword)
            .and().I_am().on_user_edit_page();

        when()
            .I().change_my_password_to(newPassword, oldPassword);

        then()
            .I().see_my_profile_page()
            .and().password_is_changed();

    }
}
