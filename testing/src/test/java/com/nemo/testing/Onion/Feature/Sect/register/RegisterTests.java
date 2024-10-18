package com.nemo.testing.Onion.Feature.Sect.register;

import com.nemo.testing.core.OnionTest;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

@OnionTest
@SuppressWarnings("ResultOfMethodCallIgnored")
public class RegisterTests extends SpringScenarioTest<RegisterGivenStage, RegisterWhenStage, RegisterThenStage> {

    private static final String DEFAULT_USERNAME = "myTestUsername";
    private static final String DEFAULT_PASSWORD = "myTestPassword";

    @Test
    void register_new_user() {

        given()
            .I_am().on_register_page()
            .and().not_logged_in()
            .and().my_account_does_not_exist(DEFAULT_USERNAME);

        when()
            .I().register_with_credentials(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        then()
            .waitUntilRequestProcessed()
            .my_account_is_created(DEFAULT_USERNAME)
            .and().I_am().on_home_page()
            .and().I_am().logged_in();

    }

    @Test
    void user_already_exists() {

        given()
            .I_am().on_register_page()
            .and().I_am().not_logged_in()
            .and().account_$_already_exists(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        when()
            .I().register_with_credentials(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        then()
            .I().see_a_name_has_been_taken_message()
            .and().I_am().not_logged_in();

    }

    @Test
    void passwords_do_not_match() {

        given()
            .I_am().on_register_page()
            .and().I_am().not_logged_in();

        when()
            .I().enter_different_passwords("password", "otherPassword");

        then()
            .I().see_a_passwords_do_not_match_message()
            .and().register_button_is_disabled();

    }
}
