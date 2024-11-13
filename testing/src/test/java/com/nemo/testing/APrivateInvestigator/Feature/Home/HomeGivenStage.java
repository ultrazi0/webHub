package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.core.Persistence.RobotService;
import com.nemo.webHub.Decibel.RobotEntity;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class HomeGivenStage extends AbstractGivenStage<HomeGivenStage> {

    @Autowired
    private RobotService robotService;

    public HomeGivenStage request_values_of_command(@Quoted String commandType) {
        request.queryParam("commandType", commandType);

        return self();
    }

    public HomeGivenStage have_a_robot(@Quoted String robotName) {
        createdEntities.addInstance(robotService.createNewRobot(robotName, CURRENT_USER.getId()));

        return self();
    }

    public HomeGivenStage request_it() {
        Set<RobotEntity> createdRobots = createdEntities.getInstances(RobotEntity.class);
        assumeThat(createdRobots)
            .as("Only one robot should be created")
            .hasSize(1);

        request.pathParam("robotId", createdRobots.iterator().next().getId());

        return self();
    }
}
