package com.nemo.testing.APrivateInvestigator.Feature.Sect.login;

import com.nemo.testing.core.APrivateInvestigatorTest;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

@APrivateInvestigatorTest
class LoginTests extends SpringScenarioTest<LoginGivenStage, LoginWhenStage, LoginThenStage> {

    @Test
    void given_correct_credentials_should_log_in() {
        String username = "user";
        String password = "password";

        given()
            .I_have().correct_credentials(username, password)
            .and().user_$_exists(username, password);

        when()
            .I().send_request_to().login_endpoint();

        then()
            .response_is_correct()
            .and().I_am().logged_in_as(username);
    }

    @Test
    void login_should_fail_when_incorrect_credentials_are_provided() {

        given()
            .I_have().incorrect_credentials("user", "password1");

        when()
            .I().send_request_to().login_endpoint();

        then()
            .get_an_error_that().credentials_are_incorrect()
            .and().I_am().not_logged_in();
    }

    @Test
    void once_logged_in_should_be_able_to_log_out() {
        String username = "myTestUser";
        String password = "myTestPassword";

        given()
            .user_$_exists(username, password)
            .and().I_am().logged_in_as_$_with_password(username, password);

        when()
            .I().send_request_to().logout_endpoint();

        then()
            .logout_response_is_correct()
            .and().I_am().not_logged_in();

    }
}
