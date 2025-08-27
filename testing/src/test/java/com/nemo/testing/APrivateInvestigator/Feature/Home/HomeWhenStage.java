package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractWhenStage;
import com.nemo.testing.APrivateInvestigator.Model.Endpoints.WithHomeEndpoints;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class HomeWhenStage extends AbstractWhenStage<HomeWhenStage> implements WithHomeEndpoints {

    public HomeWhenStage get_robot_commands_endpoint() {
        request.to(COMMANDS_ENDPOINT);

        return sendAndReturnSelf();
    }

    public HomeWhenStage insert_command_endpoint() {
        request.to(INSERT_COMMANDS_ENDPOINT);

        return sendAndReturnSelf();
    }

    public HomeWhenStage delete_command_endpoint() {
        request.to(DELETE_COMMANDS_ENDPOINT);

        return sendAndReturnSelf();
    }

    public HomeWhenStage get_robot_endpoint() {
        request.to(GET_ROBOT_ENDPOINT);

        return sendAndReturnSelf();
    }

    public HomeWhenStage insert_robot_endpoint() {
        request.to(INSERT_ROBOT_ENDPOINT);

        return sendAndReturnSelf();
    }

    public HomeWhenStage edit_robot_endpoint() {
        request.to(EDIT_ROBOT_ENDPOINT);

        return sendAndReturnSelf();
    }

    public HomeWhenStage delete_robot_endpoint() {
        request.to(DELETE_ROBOT_ENDPOINT);

        return sendAndReturnSelf();
    }

    public HomeWhenStage get_user_robots_endpoint() {
        request.to(GET_USER_ROBOTS_ENDPOINT);

        return sendAndReturnSelf();
    }
}
