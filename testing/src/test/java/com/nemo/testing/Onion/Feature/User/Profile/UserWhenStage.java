package com.nemo.testing.Onion.Feature.User.Profile;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.User.UserPage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class UserWhenStage extends AbstractWhenStage<UserWhenStage> {

    @Autowired
    private UserPage userPage;

    @Override
    protected AbstractPage mainPage() {
        return userPage;
    }

    public UserWhenStage press_the_edit_button() {
        userPage.pressEditButton();

        return self();
    }
}
