package com.nemo.testing.Onion.Feature.Sect.login;

import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.Onion.Model.Sect.LoginPage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class LoginPageThenStage extends AbstractThenStage<LoginPageThenStage, LoginPage> {

    @Override
    protected AbstractPage mainPage() {
        return mainPage;
    }

    public LoginPageThenStage I_expect_username_$_in_top_right_corner(@Quoted String username) {
        String currentUserName = mainPage.getUsernameFromNavbar();

        assertThat(currentUserName, "Usernames do not match").isEqualTo(username);

        return self();
    }

    public LoginPageThenStage I_cannot_log_in() {
        assertThat(mainPage.shouldSeeWrongCredentialsMessage(), "No wrong credentials message").isTrue();

        return self();
    }
}
