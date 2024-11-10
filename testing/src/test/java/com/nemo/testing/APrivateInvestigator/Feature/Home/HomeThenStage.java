package com.nemo.testing.APrivateInvestigator.Feature.Home;

import com.nemo.testing.APrivateInvestigator.Feature.AbstractStages.AbstractThenStage;
import com.nemo.webHub.Commands.CommandType;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.hamcrest.Matchers;

import java.util.Arrays;
import java.util.List;

@JGivenStage
class HomeThenStage extends AbstractThenStage<HomeThenStage> {

    public HomeThenStage get_all_commands() {
        List<String> expectedList = Arrays.stream(CommandType.values()).map(CommandType::toString).toList();

        validatableResponse.body("", Matchers.equalTo(expectedList));

        return self();
    }

    public HomeThenStage get_values_for_command(@Quoted String command) {
        List<String> expectedValues = Arrays.stream(CommandType.valueOf(command).getKeys()).toList();

        validatableResponse.body("", Matchers.equalTo(expectedValues));

        return self();
    }
}
