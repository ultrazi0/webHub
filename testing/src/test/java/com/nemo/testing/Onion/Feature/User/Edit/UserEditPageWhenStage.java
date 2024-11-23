package com.nemo.testing.Onion.Feature.User.Edit;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.User.UserEditPage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class UserEditPageWhenStage extends AbstractWhenStage<UserEditPageWhenStage> {

    @Autowired
    private UserEditPage userEditPage;

    @Override
    protected AbstractPage mainPage() {
        return userEditPage;
    }

    public UserEditPageWhenStage change_my_username_to(@Quoted String newUsername, @Hidden String password) {
        userEditPage.enterUsername(newUsername);
        userEditPage.enterOldPassword(password);
        userEditPage.pressSubmitButton();

        assertTakingScreenshotThat(userEditPage.isSubmitted(), "Form must be submitted")
            .withFailMessage("The submit button has not disappeared")
            .isTrue();

        return self();
    }

    public UserEditPageWhenStage change_my_password_to(String newPassword, String oldPassword) {
        userEditPage.enterOldPassword(oldPassword);
        userEditPage.enterNewPassword(newPassword);
        userEditPage.enterRepeatPassword(newPassword);
        userEditPage.pressSubmitButton();

        assertTakingScreenshotThat(userEditPage.isSubmitted(), "Form must be submitted")
            .withFailMessage("The submit button has not disappeared")
            .isTrue();

        return self();
    }
}
