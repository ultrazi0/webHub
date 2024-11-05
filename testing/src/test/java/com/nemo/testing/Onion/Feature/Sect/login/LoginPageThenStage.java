package com.nemo.testing.Onion.Feature.Sect.login;

import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.Onion.Model.Sect.LoginPage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class LoginPageThenStage extends AbstractThenStage<LoginPageThenStage> {

    @Autowired
    private LoginPage loginPage;

    @Override
    protected AbstractPage mainPage() {
        return loginPage;
    }

    public LoginPageThenStage I_expect_username_$_in_top_right_corner(@Quoted String username) {
        String currentUserName = loginPage.getUsernameFromNavbar();

        assertTakingScreenshotThat(currentUserName, "Check logged in username")
            .withFailMessage("Usernames do not match")
            .isEqualTo(username);

        return self();
    }

    public LoginPageThenStage I_cannot_log_in() {
        assertTakingScreenshotThat(loginPage.shouldSeeWrongCredentialsMessage(), "Check wrong credentials message")
            .withFailMessage("No wrong credentials message")
            .isTrue();

        return self();
    }
}
