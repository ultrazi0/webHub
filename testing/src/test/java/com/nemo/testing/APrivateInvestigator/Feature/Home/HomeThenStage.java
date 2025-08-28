package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.core.Persistence.RobotService;
import com.nemo.webHub.Commands.CustomCommandType;
import com.nemo.webHub.Commands.StandardCommandType;
import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.RobotNotFoundException;
import com.nemo.webHub.User.User;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
class HomeThenStage extends AbstractThenStage<HomeThenStage> {

    private final RobotService robotService;

    public HomeThenStage(RobotService robotService) {
        super();
        this.robotService = robotService;
    }

    public HomeThenStage get_a_list_of_only_standard_commands() {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<?, ?>[] expectedCommandTypes = Arrays.stream(StandardCommandType.values())
            .map(commandType -> objectMapper.convertValue(commandType, Map.class))
            .toArray(Map[]::new);

        validatableResponse.body("", Matchers.containsInAnyOrder(expectedCommandTypes));

        return self();
    }

    @NestedSteps
    public HomeThenStage get_the_correct_robot() {
        RobotEntity robot = retrieveCreatedRobotEntity();

        return id_is(robot.getId())
            .and().name_is(robot.getName())
            .and().password_is(robot.getPassword().replaceFirst("\\{noop}", ""))
            .and().created_at_is(robot.getCreatedAt())
            .and().owner_id_is(robot.getOwner().getId())
            .and().online_status_is(robot.isOnline());
    }

    public HomeThenStage id_is(@Quoted int id) {
        validatableResponse.body("id", Matchers.equalTo(id));

        return self();
    }

    public HomeThenStage name_is(@Quoted String name) {
        validatableResponse.body("name", Matchers.equalTo(name));

        return self();
    }

    public HomeThenStage password_is(@Quoted String password) {
        validatableResponse.body("password", Matchers.equalTo(password));

        return self();
    }

    public HomeThenStage created_at_is(@Quoted OffsetDateTime created_at) {
        validatableResponse.body(
            "createdAt.with { java.time.OffsetDateTime.parse(it).toInstant() }",
            Matchers.equalTo(created_at.toInstant())
        );

        return self();
    }

    public HomeThenStage owner_id_is(@Quoted int owner_id) {
        validatableResponse.body("owner.id", Matchers.equalTo(owner_id));

        return self();
    }

    public HomeThenStage online_status_is(@Quoted boolean isOnline) {
        validatableResponse.body("online", Matchers.equalTo(isOnline));

        return self();
    }

    public HomeThenStage the_retrieved_robot_has_standard_commands_as_well_as_custom_ones(@Hidden CustomCommandType[] customRobotCommands) {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<?, ?>[] expectedCommandTypes = Stream.concat(
            Arrays.stream(StandardCommandType.values()),
            Arrays.stream(customRobotCommands)
        ).map(commandType -> objectMapper.convertValue(commandType, Map.class))
            .toArray(Map[]::new);

        validatableResponse.body("commands", Matchers.containsInAnyOrder(expectedCommandTypes));

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage robot_$_is_created(String robotName) {
        assertThatCode(() -> robotService.findRobotByName(robotName, CURRENT_USER.getId()))
            .as("Robot %s is created", robotName)
            .doesNotThrowAnyException();

        return self();
    }

    public HomeThenStage the_name_of_the_returned_robot_is(@Quoted String robotName) {
        validatableResponse.body("name", Matchers.equalTo(robotName));

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage its_name_is_changed_from_$_to(@Quoted String originalRobotName, @Quoted String editedRobotName) {
        return robot_with_name_$_does_not_exit(originalRobotName)
            .and().robot_$_is_created(editedRobotName);
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage robot_with_name_$_does_not_exit(@Quoted String robotName) {
        assertThatThrownBy(() -> robotService.findRobotByName(robotName, CURRENT_USER.getId()))
            .as("Assert cannot find robot with name \"%s\"", robotName)
            .isInstanceOf(RobotNotFoundException.class);

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage the_robot_has_correct_custom_commands(@Hidden CustomCommandType... customCommands) {
        RobotEntity robot = retrieveCreatedRobotEntity();

        List<CustomCommandType> customRobotCommands = robotService.getCustomRobotCommands(robot.getId());

        assertThat(customRobotCommands)
            .as("Assert exactly %s custom commands have been created", customCommands.length)
            .hasSize(customCommands.length)
            .as("Assert the robot has exactly the provided commands")
            .containsExactlyInAnyOrder(customCommands);

        return self();
    }

    public HomeThenStage get_all_my_robots(List<String> robotNames) {
        int size = robotNames.size();

        // This is not in violation of the "no ifs" principle, because it checks the *expected* values, and not *actual*
        if (size == 0) {
            validatableResponse.body("", Matchers.not(Matchers.hasKey("_embedded.robotEntityList")));

            return self();
        }

        validatableResponse.rootPath("_embedded");
        validatableResponse.body("robotEntityList.size()", Matchers.equalTo(size));
        validatableResponse.body("robotEntityList.with { it.name }", Matchers.containsInAnyOrder(robotNames.toArray()));

        return self();
    }

    @ExtendedDescription(CHECKED_IN_DATABASE)
    public HomeThenStage the_robot_is_shared_with(String... usernames) {
        RobotEntity robot = retrieveCreatedRobotEntity();

        List<User> sharedUsers = robotService.getSharedUsers(robot.getId(), robot.getOwner().getId());

        assertThat(sharedUsers)
            .as("Assert exactly %s users have been shared with the robot", usernames.length)
            .hasSize(usernames.length)
            .as("Assert the shared users are exactly the provided users")
            .extracting(User::getUsername)
            .containsExactlyInAnyOrder(usernames);

        return self();
    }

    private RobotEntity retrieveCreatedRobotEntity() {
        Set<RobotEntity> createdRobots = createdEntities.getInstances(RobotEntity.class);
        Assertions.assertThat(createdRobots)
            .as("Only one robot should have been created")
            .hasSize(1);

        return createdRobots.iterator().next();
    }
}
