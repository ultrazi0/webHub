package com.nemo.testing.APrivateInvestigator.Feature.Sect.register;

import com.nemo.testing.core.API.WithErrorMessages;
import com.nemo.testing.core.APrivateInvestigatorTest;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

@APrivateInvestigatorTest
@SuppressWarnings("ResultOfMethodCallIgnored")
class RegisterTests extends SpringScenarioTest<RegisterGivenStage, RegisterWhenStage, RegisterThenStage>
    implements WithErrorMessages {

    private static final String DEFAULT_USERNAME = "myTestUsername";
    private static final String DEFAULT_PASSWORD = "myTestPassword";

    @Test
    void provided_correct_credentials_should_be_able_to_register() {

        given()
            .account_$_does_not_exist(DEFAULT_USERNAME)
            .and().I().supply_correct_credentials(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        when()
            .I().send_request_to().register_endpoint();

        then()
            .new_user_$_is_created(DEFAULT_USERNAME)
            .and().response_is_correct(DEFAULT_USERNAME)
            .and().I_am().logged_in_as(DEFAULT_USERNAME);

    }

    @Test
    void provided_incorrect_credentials_attempt_to_register_should_fail() {

        given()
            .user_$_exists(DEFAULT_USERNAME, DEFAULT_PASSWORD)
            .and().I().supply_incorrect_credentials(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        when()
            .I().send_request_to().register_endpoint();

        then()
            .response_is_correct(409)
            .and().I().get_an_error(NAME_ALREADY_TAKEN);

    }

}
