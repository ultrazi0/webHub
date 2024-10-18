package com.nemo.testing.Onion.Feature.Sect.login;

import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.Onion.Model.Sect.LoginPage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

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
        assumeRendered(loginPage);

        return self();
    }
}
