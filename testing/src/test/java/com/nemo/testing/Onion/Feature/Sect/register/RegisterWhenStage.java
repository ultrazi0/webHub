package com.nemo.testing.Onion.Feature.Sect.register;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Sect.RegisterPage;
import com.nemo.webHub.Decibel.UserEntity;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThatCode;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class RegisterWhenStage extends AbstractWhenStage<RegisterWhenStage> {

    @Autowired
    private RegisterPage registerPage;

    @Override
    protected AbstractPage mainPage() {
        return registerPage;
    }

    public RegisterWhenStage register_with_credentials(@Quoted String username, @Quoted String password) {
        return I().try_to_register_with_credentials(username, password)
            .and().I().registerEntity(UserEntity.class, username);
    }

    public RegisterWhenStage try_to_register_with_credentials(@Quoted String username, @Quoted String password) {

        registerPage.enterCredentials(username, password);
        registerPage.pressSubmit();

        waitUntilRequestProcessed();

        return self();
    }

    public RegisterWhenStage enter_different_passwords(@Hidden String password, @Hidden String otherPassword) {
        registerPage.enterPassword(password);
        registerPage.enterRepeatPassword(otherPassword);

        return self();
    }

    @Hidden
    private RegisterWhenStage waitUntilRequestProcessed() {

        assertThatCode(() ->
            driverService.waitUntil(driver ->
                !registerPage.registerButtonIsDisplayed() || registerPage.nameHasBeenTakenMessageIsDisplayed()))
            .as("Wait until request register processed")
            .doesNotThrowAnyException();

        return self();
    }
}
