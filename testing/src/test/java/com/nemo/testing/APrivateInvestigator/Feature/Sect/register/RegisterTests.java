package com.nemo.testing.APrivateInvestigator.Feature.Sect.register;

import com.nemo.testing.core.APrivateInvestigatorTest;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;

@APrivateInvestigatorTest
class RegisterTests extends SpringScenarioTest<RegisterGivenStage, RegisterWhenStage, RegisterThenStage> {

    private static final String DEFAULT_USERNAME = "myTestUsername";
    private static final String DEFAULT_PASSWORD = "myTestPassword";

    @Test
    void register() {

        given()
            .account_$_does_not_exist(DEFAULT_USERNAME)
            .and().I().supply_correct_credentials(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        when()
            .I().send_request_to().register_endpoint();

        then()
            .response_is_correct(DEFAULT_USERNAME);
    }

}
