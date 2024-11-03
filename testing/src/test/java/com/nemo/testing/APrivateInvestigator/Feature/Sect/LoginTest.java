package com.nemo.testing.APrivateInvestigator.Feature.Sect;

import com.nemo.testing.core.APrivateInvestigatorTest;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

@APrivateInvestigatorTest
public class LoginTest extends SpringScenarioTest<LoginGivenStage, LoginWhenStage, LoginThenStage> {

    @Test
    void test() {
        RequestSpecification request = RestAssured.given();
        request.log().all();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.basePath("/api");
        Response response = request.get("/csrf");
        response.then().statusCode(200);
        response.getBody().prettyPrint();
    }

    @Test
    void test1() {
        given()
            .I_have().correct_credentials("user", "password");

        when()
            .I().send_request_to().login_endpoint();

        then()
            .response_is_correct()
            .and().I_am().logged_in_as("user");
    }
}
