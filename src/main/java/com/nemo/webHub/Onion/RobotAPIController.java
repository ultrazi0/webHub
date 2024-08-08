package com.nemo.webHub.Onion;

import com.nemo.webHub.Commands.CommandType;
import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.RobotRepository;
import com.nemo.webHub.Decibel.UserEntity;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class RobotAPIController {

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
        UserEntity user = readCurrentUserFromContext();

        EntityModel<RobotEntity> robotEntityModel = robotModelAssembler
                .toModel(robotRepository.insertNewRobot(name, user.getId()));

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
    public CollectionModel<EntityModel<RobotEntity>> getUserRobots() {

        UserEntity user = readCurrentUserFromContext();

        List<EntityModel<RobotEntity>> robots = Arrays.stream(robotRepository.getUserRobots(user.getId()))
                .map(robotModelAssembler::toModel).toList();

        return CollectionModel.of(robots, linkTo(methodOn(this.getClass()).getUserRobots()).withSelfRel());
    }

    private UserEntity readCurrentUserFromContext() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserEntity) {
            return (UserEntity) principal;
        }
        throw new IllegalArgumentException("Principal is not an instance of UserEntity");
    }
}
