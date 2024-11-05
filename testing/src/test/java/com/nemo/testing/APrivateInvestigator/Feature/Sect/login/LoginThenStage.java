package com.nemo.testing.APrivateInvestigator.Feature.Sect.login;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.core.API.Request;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;

@JGivenStage
class LoginThenStage extends AbstractThenStage<LoginThenStage> {

    @As("response is correct")
    public LoginThenStage logout_response_is_correct() {
        validatableResponse.statusCode(204);

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

    public LoginThenStage credentials_are_incorrect() {
        validatableResponse.statusCode(401);

        return self();
    }

    public LoginThenStage not_logged_in() {

        apiService.sendGet(Request.createTo("/user"))
            .then()
            .statusCode(401);

        return self();
    }
}
