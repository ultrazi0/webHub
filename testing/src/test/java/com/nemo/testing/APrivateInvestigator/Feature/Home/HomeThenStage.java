package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractThenStage;
import com.nemo.webHub.Commands.CommandType;
import com.nemo.webHub.Decibel.RobotEntity;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@JGivenStage
class HomeThenStage extends AbstractThenStage<HomeThenStage> {

    public HomeThenStage get_all_commands() {
        List<String> expectedList = Arrays.stream(CommandType.values()).map(CommandType::toString).toList();

        validatableResponse.body("", Matchers.equalTo(expectedList));

        return self();
    }

    public HomeThenStage get_values_for_command(@Quoted String command) {
        List<String> expectedValues = Arrays.stream(CommandType.valueOf(command).getKeys()).toList();

        // TODO: find a way to show the expected values in the report
        validatableResponse.body("", Matchers.equalTo(expectedValues));

        return self();
    }

    @NestedSteps
    public HomeThenStage get_the_correct_robot() {
        Set<RobotEntity> createdRobots = createdEntities.getInstances(RobotEntity.class);
        Assertions.assertThat(createdRobots)
            .as("Only one robot should have been created")
            .hasSize(1);

        RobotEntity robot = createdRobots.iterator().next();

        return id_is(robot.getId())
            .and().name_is(robot.getName())
            .and().password_is(robot.getPassword().replaceFirst("\\{noop}", ""))
            .and().created_at_is(robot.getCreatedAt())
            .and().owner_id_is(robot.getOwnerId())
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
        validatableResponse.body("createdAt.with { java.time.OffsetDateTime.parse(it) }", Matchers.equalTo(created_at));

        return self();
    }

    public HomeThenStage owner_id_is(@Quoted int owner_id) {
        validatableResponse.body("ownerId", Matchers.equalTo(owner_id));

        return self();
    }

    public HomeThenStage online_status_is(@Quoted boolean isOnline) {
        validatableResponse.body("online", Matchers.equalTo(isOnline));

        return self();
    }
}
