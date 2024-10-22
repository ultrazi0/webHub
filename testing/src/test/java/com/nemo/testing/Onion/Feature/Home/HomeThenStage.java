package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractThenStage;
import com.nemo.testing.Onion.Model.AbstractPage;
import com.nemo.testing.Onion.Model.Home.HomePage;
import com.nemo.testing.core.Persistence.RobotService;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class HomeThenStage extends AbstractThenStage<HomeThenStage> {

    @ExpectedScenarioState
    private Set<Integer> createdRobots;

    @Autowired
    private HomePage homePage;
    @Autowired
    private RobotService robotService;

    @Override
    protected AbstractPage mainPage() {
        return homePage;
    }

    public HomeThenStage see_robot_$_as_a_card(@Quoted String robotName) {
        assertTakingScreenshotThat(homePage.thereIsARobotCardWithName(robotName),
            "Check the existence of a robot card")
            .isTrue();

        return self();
    }

    @ExtendedDescription("Checked in the database")
    public HomeThenStage robot_is_created(String robotName) {
        assertThatCode(() -> createdRobots.add(robotService.findRobotIdByName(robotName)))
            .as("Check in the database if the robot is created")
            .doesNotThrowAnyException();

        return self();
    }
}
