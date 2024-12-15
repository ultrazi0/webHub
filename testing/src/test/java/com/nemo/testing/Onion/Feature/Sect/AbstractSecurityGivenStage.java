package com.nemo.testing.Onion.Feature.Sect;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.webHub.Decibel.UserEntity;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.jooq.generated.tables.records.UsersRecord;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractSecurityGivenStage<T extends AbstractSecurityGivenStage<T>> extends AbstractGivenStage<T> {

    @ExtendedDescription("Checked in the database")
    public T account_$_already_exists(@Quoted String username, @Hidden String password) {
        UsersRecord usersRecord = new UsersRecord();
        usersRecord.setUsername(username);
        usersRecord.setPassword(password);

        // noinspection ResultOfMethodCallIgnored
        createEntity(UserEntity.class, usersRecord);

        return self();
    }
}
