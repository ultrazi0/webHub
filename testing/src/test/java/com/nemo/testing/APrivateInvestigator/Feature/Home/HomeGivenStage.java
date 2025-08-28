package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.APrivateInvestigator.Model.Formatters.CustomCommandTypeArrayFormatter;
import com.nemo.testing.APrivateInvestigator.Model.Formatters.CustomCommandTypeFormatter;
import com.nemo.testing.core.Persistence.RobotService;
import com.nemo.testing.core.Persistence.UniqueAttributes.RobotUniqueAttributes;
import com.nemo.webHub.Commands.CustomCommandType;
import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.RobotNotFoundException;
import com.nemo.webHub.Onion.RobotAPIController;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import io.restassured.http.ContentType;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class HomeGivenStage extends AbstractGivenStage<HomeGivenStage> {

    @Autowired
    private RobotService robotService;

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeGivenStage have_a_robot(@Quoted String robotName) {
        RobotsRecord robotsRecord = new RobotsRecord();
        robotsRecord.setName(robotName);
        robotsRecord.setOwnerId(CURRENT_USER.getId());

        assumeThatCode(() -> createEntity(RobotEntity.class, robotsRecord))
            .as("Try to create a new robot \"%s\"", robotName)
            .doesNotThrowAnyException();

        return self();
    }

    public HomeGivenStage request_it() {
        return requestRobot(getCreatedRobot().getId());
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeGivenStage robot_$_does_not_exist(@Quoted String robotName) {
        assumeThatThrownBy(() -> robotService.findRobotByName(robotName, CURRENT_USER.getId()))
            .as("Check \"%s\" does not exist", robotName)
            .isInstanceOf(RobotNotFoundException.class);

        return self();
    }

    @As("set new robot's name to")
    public HomeGivenStage set_new_robots_name_to(@Quoted String robotName) {
        request.formParam("name", robotName);

        setEntityToRegisterType(RobotEntity.class);
        setEntityToRegisterUniqueAttributes(RobotUniqueAttributes.byRobotNameAndOwnerId(robotName, CURRENT_USER.getId()));

        return self();
    }

    public HomeGivenStage change_its_name_to(@Quoted String robotName) {

        request.contentType(ContentType.JSON);
        request.body(new RobotAPIController.EditRobotRequest(robotName, List.of()));

        return request_it();
    }

    public HomeGivenStage request_to_change_its_commands_to(CustomCommandType... customCommands) {
        RobotEntity robot = getCreatedRobot();

        request.contentType(ContentType.JSON);
        request.body(new RobotAPIController.EditRobotRequest(robot.getName(), List.of(customCommands)));

        return requestRobot(robot.getId());
    }

    public HomeGivenStage want_to_delete_it() {
        return request_it();
    }

    public HomeGivenStage have_robots(List<String> robotNames) {
        assumeThatCode(() -> createdEntities.addInstances(
            RobotEntity.class, robotService.createNewRobots(robotNames, CURRENT_USER.getId())))
            .as("Try to create new robots %s", robotNames)
            .doesNotThrowAnyException();

        return self();
    }

    public HomeGivenStage it_has_custom_commands(@Format(CustomCommandTypeArrayFormatter.class) CustomCommandType... customCommands) {
        int robotId = getCreatedRobot().getId();
        robotService.createCustomCommands(robotId, List.of(customCommands));

        assumeThat(robotService.getCustomRobotCommands(robotId))
            .as("Assume commands are created")
            .containsExactlyInAnyOrder(customCommands);

        return self();
    }

    public HomeGivenStage create_the_$_command(@Format(CustomCommandTypeFormatter.class) CustomCommandType customCommand) {
        request.formParam("commandType", customCommand.getCommandType());
        request.formParam("commandKeys", (Object[]) customCommand.getKeys());

        return request_it();
    }

    public HomeGivenStage request_to_delete_the_$_command(@Quoted String customCommandType) {
        request.formParam("commandType", customCommandType);

        return request_it();
    }

    public HomeGivenStage want_to_share_it_with(String... usernames) {
        request.formParam("users", String.join(",", usernames));

        return request_it();
    }

    @Hidden
    public HomeGivenStage requestRobot(int robotId) {
        request.pathParam("robotId", robotId);

        return self();
    }

    private RobotEntity getCreatedRobot() {
        Set<RobotEntity> createdRobots = createdEntities.getInstances(RobotEntity.class);
        assumeThat(createdRobots)
            .as("Only one robot should be created")
            .hasSize(1);

        return createdRobots.iterator().next();
    }
}
