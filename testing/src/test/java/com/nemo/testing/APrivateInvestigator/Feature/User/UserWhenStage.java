package com.nemo.testing.APrivateInvestigator.Feature.User;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.APrivateInvestigator.Model.Endpoints.WithUserEndpoints;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class UserWhenStage extends AbstractWhenStage<UserWhenStage> implements WithUserEndpoints {

    public UserWhenStage edit_user_endpoint() {
        request.to(EDIT_USER_ENDPOINT);

        return sendAndReturnSelf();
    }

    public UserWhenStage delete_user_endpoint() {
        request.to(DELETE_USER_ENDPOINT);

        return sendAndReturnSelf();
    }

    public UserWhenStage get_user_endpoint() {
        request.to(USER_ENDPOINT);

        return sendAndReturnSelf();
    }
}
