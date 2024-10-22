package com.nemo.testing.Onion.Feature.Home;

import com.nemo.testing.Onion.Feature.AbstractStages.AbstractGivenStage;
import com.nemo.testing.Onion.Model.Home.HomePage;
import com.nemo.testing.core.Persistence.RobotService;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@JGivenStage
@SuppressWarnings("UnusedReturnValue")
public class HomeGivenStage extends AbstractGivenStage<HomeGivenStage> {

    @ProvidedScenarioState
    private final Set<Integer> createdRobots = new HashSet<>();

    @Autowired
    private Logger log;
    @Autowired
    private HomePage homePage;
    @Autowired
    private RobotService robotService;

    @Override
    protected HomePage mainPage() {
        return homePage;
    }

    @AfterScenario
    private void deleteCreatedRobots() {
        for (Integer robotId : createdRobots) {
            try {
                robotService.deleteRobotById(robotId);
            } catch (RuntimeException ignored) {
                log.warn("Could not delete robot  with ID {} because it does not exist, proceeding as is", robotId);
            } finally {
                createdRobots.remove(robotId);
            }
        }
    }

    public HomeGivenStage on_home_page() {
        homePage.openPage();
        assumeOnMainPage();
        assumeRendered(homePage);

        return self();
    }

    public HomeGivenStage see_add_robot_button() {
        assumeTakingScreenshotThat(homePage.canSeeAddRobotButton(),
            "Check if can see add robot button")
            .withFailMessage("No add robot button - try logging in")
            .isTrue();

        return self();
    }

    public HomeGivenStage robot_with_name_$_does_not_exist(@Quoted String robotName) {
        assumeThatThrownBy(() -> robotService.findRobotIdByName(robotName))
            .as("Check if robot with name \"%s\" does not exist (name should be unique among all users)", robotName)
            .isInstanceOf(RuntimeException.class);

        return self();
    }
}
