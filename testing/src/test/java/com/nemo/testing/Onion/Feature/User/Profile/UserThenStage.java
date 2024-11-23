package com.nemo.testing.Onion.Feature.User.Profile;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.User.UserEditPage;
import com.nemo.testing.Onion.Model.User.UserPage;
import com.nemo.testing.core.Persistence.UserService;
import com.nemo.webHub.Decibel.UserEntity;
import com.nemo.webHub.Decibel.UserNotFoundException;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static com.codeborne.selenide.WebDriverRunner.url;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class UserThenStage extends AbstractThenStage<UserThenStage> {

    @Autowired
    private UserPage userPage;
    @Autowired
    private UserEditPage userEditPage;
    @Autowired
    private UserService userService;

    @Override
    protected AbstractPage mainPage() {
        return userPage;
    }

    public UserThenStage see_my_name_in_the_greeting() {
        assertTakingScreenshotThat(userPage.getUserGreeting(), "Assert username shown")
            .contains("Hi there")
            .contains(CURRENT_USER.getUsername());

        return self();
    }

    public UserThenStage on_user_edit_page() {
        assertTakingScreenshotThat(url(), "Assert on user edit page")
            .isEqualTo(fullUrlOf(userEditPage.uri()));

        return self();
    }

    public UserThenStage see_their_username() {
        Set<UserEntity> createdUsers = createdEntities.getInstances(UserEntity.class);
        assertThat(createdUsers)
            .as("Assert only one user is created")
            .hasSize(1);

        assertTakingScreenshotThat(userPage.getUserGreeting(), "Assert username shown")
            .isEqualTo(createdUsers.iterator().next().getUsername());

        return self();
    }

    public UserThenStage see_no_edit_button() {
        assertTakingScreenshotThat(userPage.editButtonIsDisplayed(), "Assert cannot see the edit button")
            .isFalse();

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public UserThenStage my_user_is_deleted() {
        assertThatThrownBy(() -> userService.getUserById(CURRENT_USER.getId()))
            .as("Assert user deleted")
            .isInstanceOf(UserNotFoundException.class);

        return self();
    }
}
