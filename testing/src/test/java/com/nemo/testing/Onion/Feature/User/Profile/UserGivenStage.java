package com.nemo.testing.Onion.Feature.User.Profile;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.User.UserPage;
import com.nemo.webHub.Decibel.UserEntity;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.jooq.generated.tables.records.UsersRecord;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class UserGivenStage extends AbstractGivenStage<UserGivenStage> {

    @Autowired
    private UserPage userPage;

    @Override
    protected AbstractPage mainPage() {
        return userPage;
    }

    public UserGivenStage on_my_user_page() {
        open(userPage);

        return self();
    }

    public UserGivenStage see_the_edit_button() {
        assumeTakingScreenshotThat(userPage.editButtonIsDisplayed(),
            "Edit button should be displayed")
            .withFailMessage("Edit button is not visible")
            .isTrue();

        return self();
    }

    @As("visit $'s profile")
    public UserGivenStage visit_$s_profile(String username) {
        UsersRecord usersRecord = new UsersRecord();
        usersRecord.setUsername(username);

        UserEntity otherUser = createEntity(UserEntity.class, usersRecord);

        userPage.openPage(otherUser.getId());
        assumeOnPage(userPage);
        assumeRendered(userPage);

        return self();
    }
}
