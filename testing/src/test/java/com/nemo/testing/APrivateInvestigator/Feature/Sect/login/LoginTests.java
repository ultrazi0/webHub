package com.nemo.testing.APrivateInvestigator.Feature.Sect.login;

import com.nemo.testing.core.APrivateInvestigatorTest;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

@APrivateInvestigatorTest
class LoginTests extends SpringScenarioTest<LoginGivenStage, LoginWhenStage, LoginThenStage> {

    private static final String DEFAULT_USERNAME = "myTestUsername";
    private static final String DEFAULT_PASSWORD = "myTestPassword";

    @Test
    void given_correct_credentials_should_log_in() {

        given()
            .I_have().correct_credentials(DEFAULT_USERNAME, DEFAULT_PASSWORD)
            .and().user_$_exists(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        when()
            .I().send_request_to().login_endpoint();

        then()
            .response_is_correct()
            .and().I_am().logged_in_as(DEFAULT_USERNAME);

    }

    @Test
    void login_should_fail_when_incorrect_credentials_are_provided() {

        given()
            .I_have().incorrect_credentials(DEFAULT_USERNAME, "password1");

        when()
            .I().send_request_to().login_endpoint();

        then()
            .get_an_error_that().credentials_are_incorrect()
            .and().I_am().not_logged_in();

    }

    @Test
    void once_logged_in_should_be_able_to_log_out() {

        given()
            .user_$_exists(DEFAULT_USERNAME, DEFAULT_PASSWORD)
            .and().I_am().logged_in_as_$_with_password(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        when()
            .I().send_request_to().logout_endpoint();

        then()
            .response_is_correct(204)
            .and().I_am().not_logged_in();

    }
}
