package com.nemo.testing.Onion.Feature.Sect.register;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Sect.RegisterPage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class RegisterWhenStage extends AbstractWhenStage<RegisterWhenStage> {

    @Autowired
    private RegisterPage registerPage;

    @Override
    protected AbstractPage mainPage() {
        return registerPage;
    }

    public RegisterWhenStage register_with_credentials(@Quoted String username, @Quoted String password) {

        registerPage.enterCredentials(username, password);
        registerPage.pressSubmit();

        // Add the user to the to-delete list
        RegisterGivenStage.createdUsers.add(username);

        return self();
    }

    public RegisterWhenStage enter_different_passwords(@Hidden String password, @Hidden String otherPassword) {
        registerPage.enterPassword(password);
        registerPage.enterRepeatPassword(otherPassword);

        return self();
    }
}
