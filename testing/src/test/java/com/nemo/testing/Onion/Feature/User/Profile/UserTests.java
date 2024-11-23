package com.nemo.testing.Onion.Feature.User.Profile;

import com.nemo.testing.core.OnionTest;
import com.nemo.testing.core.WithDefaultCredentials;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

@OnionTest
@SuppressWarnings("ResultOfMethodCallIgnored")
class UserTests extends SpringScenarioTest<UserGivenStage, UserWhenStage, UserThenStage>
    implements WithDefaultCredentials {

    @Test
    void given_logged_in_I_see_my_name() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_my_user_page();

        then()
            .I().see_my_name_in_the_greeting();
    }

    @Test
    void given_logged_in_I_can_edit_my_profile() {

        given()
            .I_am().a().test_user()
            .and().I_am().on_my_user_page()
            .and().I().see_the_edit_button();

        when()
            .I().press_the_edit_button();

        then()
            .I_am().on_user_edit_page();

    }

    @Test
    void given_logged_in_I_can_view_other_profiles_but_cannot_edit_them() {

        given()
            .I_am().a().test_user()
            .and().I().visit_$s_profile(DEFAULT_USERNAME);

        then()
            .I().see_their_username()
            .and().I().see_no_edit_button();
    }

}
