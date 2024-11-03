package com.nemo.testing.APrivateInvestigator.Feature.Sect;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.core.API.Request;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;

@JGivenStage
public class LoginThenStage extends AbstractThenStage<LoginThenStage> {

    public LoginThenStage response_is_correct() {
        validatableResponse.statusCode(200);

        return self();
    }

    public LoginThenStage logged_in_as(String user) {

        apiService.sendGet(Request.createTo("/user"))
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", Matchers.notNullValue())
            .body("username", Matchers.equalTo(user));

        return self();
    }
}
