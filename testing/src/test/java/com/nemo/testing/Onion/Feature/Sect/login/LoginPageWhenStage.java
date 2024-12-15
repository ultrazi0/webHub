package com.nemo.testing.Onion.Feature.Sect.login;

import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.Onion.Model.Sect.LoginPage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatCode;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class LoginPageWhenStage extends AbstractWhenStage<LoginPageWhenStage> {

    @Autowired
    private LoginPage loginPage;

    @Override
    protected AbstractPage mainPage() {
        return loginPage;
    }

    public LoginPageWhenStage I_login_with_credentials(@Quoted String username, @Quoted String password) {
        loginPage.enterCredentials(username, password);
        loginPage.pressSubmit();

        return waitUntilRequestProcessed();
    }

    @Hidden
    private LoginPageWhenStage waitUntilRequestProcessed() {

        assertThatCode(() ->
            driverService.waitUntil(driver ->
                !loginPage.seeLoginButton() || loginPage.wrongCredentialsMessageIsDisplayed()))
            .as("Wait until request is processed")
            .doesNotThrowAnyException();

        return self();
    }
}
