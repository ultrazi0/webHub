package com.nemo.testing.APrivateInvestigator.Feature.User;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractGivenStage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class UserGivenStage extends AbstractGivenStage<UserGivenStage> {

    public UserGivenStage logged_in_as_$_with_password(@Quoted String username, @Quoted String password) {
        assumeLoggedIn(username, password);

        return self();
    }

    public UserGivenStage change_username_to(@Quoted String username, @Hidden String password) {
        request.formParam("username", username);

        return authorize_with_old_password(password);
    }

    public UserGivenStage change_password_to(@Quoted String newPassword, @Hidden String oldPassword) {
        request.formParam("newPassword", newPassword);
        request.formParam("newPasswordRepeat", newPassword);

        return authorize_with_old_password(oldPassword);
    }

    public UserGivenStage authorize_with_old_password(@Quoted String oldPassword) {
        request.formParam("oldPassword", oldPassword);

        return self();
    }
}
