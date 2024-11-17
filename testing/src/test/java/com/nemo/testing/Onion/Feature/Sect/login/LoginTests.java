package com.nemo.testing.Onion.Feature.Sect.login;

import com.nemo.testing.core.OnionTest;
import com.nemo.testing.core.Tags.Story;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@OnionTest
@SuppressWarnings("ResultOfMethodCallIgnored")
class LoginTests extends SpringScenarioTest<LoginPageGivenStage, LoginPageWhenStage, LoginPageThenStage> {

    @ParameterizedTest
    @CsvSource({"puppy, 1234", "user, password"})
    void when_correct_credentials_I_should_be_able_to_log_in(String username, String password) {

        given()
            .I_am().on_login_page()
            .and().I_am().not_logged_in();

        when()
            .I_login_with_credentials(username, password);

        then()
            .I_expect_username_$_in_top_right_corner(username);
    }

    @Story("HUB-2")
    @ParameterizedTest
    @CsvSource({"puppy, 12345", "user, password1"})
    void when_incorrect_credentials_attempt_to_login_should_fail(String username, String password) {

        given()
            .I_am().on_login_page()
            .and().I_am().not_logged_in();

        when()
            .I_login_with_credentials(username, password);

        then()
            .I_cannot_log_in();
    }
}
