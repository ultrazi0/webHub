package com.nemo.testing.Onion.Feature.User.Edit;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.User.UserEditPage;
import com.nemo.webHub.Decibel.UserEntity;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.jooq.generated.tables.records.UsersRecord;
import org.springframework.beans.factory.annotation.Autowired;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class UserEditPageGivenStage extends AbstractGivenStage<UserEditPageGivenStage> {

    @Autowired
    private UserEditPage userEditPage;

    @Override
    protected AbstractPage mainPage() {
        return userEditPage;
    }

    public UserEditPageGivenStage logged_in_as_a_new_user(@Quoted String username, @Quoted String password) {
        UsersRecord usersRecord = new UsersRecord();
        usersRecord.setUsername(username);
        usersRecord.setPassword(password);

        createEntity(UserEntity.class, usersRecord);

        logInAs(username, password);

        return self();
    }

    public UserEditPageGivenStage on_user_edit_page() {
        open(userEditPage);

        return self();
    }
}
