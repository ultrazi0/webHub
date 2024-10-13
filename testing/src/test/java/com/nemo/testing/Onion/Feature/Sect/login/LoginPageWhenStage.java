package com.nemo.testing.Onion.Feature.Sect.login;

import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.Onion.Model.Sect.LoginPage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class LoginPageWhenStage extends AbstractWhenStage<LoginPageWhenStage, LoginPage> {

    @Override
    protected AbstractPage mainPage() {
        return mainPage;
    }

    public LoginPageWhenStage I_login_with_credentials(@Quoted String username, @Quoted String password) {
        mainPage.enterCredentials(username, password);
        mainPage.pressSubmit();

        return self();
    }
}
