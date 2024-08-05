package com.nemo.webHub.Onion;

import com.nemo.webHub.Commands.CommandType;
import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.RobotRepository;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class OnionAPIController {

    @Autowired
    private RobotRepository robotRepository;
    @Autowired
    private RobotModelAssembler robotModelAssembler;

    @GetMapping("/getAllCommands")
    public CommandType[] getAllCommands() {

        return CommandType.values();
    }

    @Nullable
    @PostMapping("/commandValues")
    public String[] commandValues(@Nullable @RequestParam("commandType") CommandType commandType) {
        if (commandType == null) {
            return null;
        }

        return commandType.getKeys();
    }

    @GetMapping("/robots/{robotId}")
    public EntityModel<RobotEntity> getRobotById(@PathVariable int robotId) {
        RobotEntity robot = robotRepository.findRobotById(robotId);

        return robotModelAssembler.toModel(robot);
    }

    @PostMapping("/robots")
    public ResponseEntity<EntityModel<RobotEntity>> insertNewRobot(@NotNull @RequestParam("name") String name) {
        EntityModel<RobotEntity> robotEntityModel = robotModelAssembler.toModel(robotRepository.insertNewRobot(name));

        return ResponseEntity
                .created(robotEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(robotEntityModel);
    }

    @PutMapping("/robots/{robotId}")
    public ResponseEntity<EntityModel<RobotEntity>> updateRobot(
            @PathVariable int robotId, @NotNull @RequestParam("name") String name) {  // TODO: consider @RequestBody

        EntityModel<RobotEntity> robotEntityModel = robotModelAssembler.toModel(robotRepository.updateRobot(robotId, name));

        return ResponseEntity
                .created(robotEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(robotEntityModel);
    }

    @DeleteMapping("/robots/{robotId}")
    public ResponseEntity<Void> deleteRobot(@PathVariable int robotId) {
        robotRepository.deleteRobot(robotId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/robots")
    public CollectionModel<EntityModel<RobotEntity>> getAllRobots() {

        List<EntityModel<RobotEntity>> robots = Arrays.stream(robotRepository.getAllRobots())
                .map(robotModelAssembler::toModel).toList();

        return CollectionModel.of(robots, linkTo(methodOn(this.getClass()).getAllRobots()).withSelfRel());
    }
}
