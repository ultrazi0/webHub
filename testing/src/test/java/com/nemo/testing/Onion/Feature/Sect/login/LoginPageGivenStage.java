package com.nemo.testing.Onion.Feature.Sect.login;

import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.Onion.Model.Sect.LoginPage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assumptions.assumeThat;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class LoginPageGivenStage extends AbstractGivenStage<LoginPageGivenStage> {

    @Autowired
    private LoginPage loginPage;

    @Override
    protected AbstractPage mainPage() {
        return loginPage;
    }

    public LoginPageGivenStage on_login_page() {

        loginPage.openPage();
        assumeOnMainPage();

        return self();
    }

    public LoginPageGivenStage not_logged_in() {
        assumeThat(loginPage.isLoggedIn())
            .as("I am already logged in")
            .isFalse();

        return self();
    }
}
