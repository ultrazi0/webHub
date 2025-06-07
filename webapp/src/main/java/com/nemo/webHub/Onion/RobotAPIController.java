package com.nemo.webHub.Onion;

import com.nemo.webHub.Commands.CommandType;
import com.nemo.webHub.Commands.StandardCommandType;
import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.RobotRepository;
import com.nemo.webHub.Decibel.UserEntity;
import com.nemo.webHub.User.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RobotAPIController {

    private final RobotRepository robotRepository;
    private final RobotModelAssembler robotModelAssembler;

    @GetMapping("/getAllCommands")
    public CommandType[] getAllCommands() {
        return StandardCommandType.values();
    }

    @Nullable
    @GetMapping("/commandValues")
    public String[] commandValues(@Nullable @RequestParam("commandType") StandardCommandType commandType) {
        if (commandType == null) {
            return null;
        }

        return commandType.getKeys();
    }

    @GetMapping("/robots/{robotId}")
    public EntityModel<RobotEntity> getRobotById(@PathVariable int robotId, @AuthenticationPrincipal UserEntity user) {
        RobotEntity robot = robotRepository.findRobotByIdIfAllowed(robotId, user.getId());

        return robotModelAssembler.toModel(robot);
    }

    @PostMapping("/robots")
    public ResponseEntity<EntityModel<RobotEntity>> insertNewRobot(
            @NotNull @RequestParam("name") String name, @AuthenticationPrincipal UserEntity user) {

        EntityModel<RobotEntity> robotEntityModel = robotModelAssembler
                .toModel(robotRepository.insertNewRobot(name, user.getId()));

        return ResponseEntity
                .created(robotEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(robotEntityModel);
    }

    @PutMapping("/robots/{robotId}")
    public ResponseEntity<EntityModel<RobotEntity>> updateRobot(
            @PathVariable int robotId, @NotNull @RequestParam("name") String name,
            @AuthenticationPrincipal UserEntity user) {  // TODO: consider @RequestBody

        EntityModel<RobotEntity> robotEntityModel = robotModelAssembler.toModel(
                robotRepository.updateRobot(robotId, name, user.getId()));

        return ResponseEntity
                .created(robotEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(robotEntityModel);
    }

    @DeleteMapping("/robots/{robotId}")
    public ResponseEntity<Void> deleteRobot(@PathVariable int robotId, @AuthenticationPrincipal UserEntity user) {
        robotRepository.deleteRobot(robotId, user.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/robots")
    public CollectionModel<EntityModel<RobotEntity>> getUserRobots(@AuthenticationPrincipal UserEntity user) {

        List<EntityModel<RobotEntity>> robots = Arrays.stream(robotRepository.getUserRobots(user.getId()))
                .map(robotModelAssembler::toModel).toList();

        return CollectionModel.of(robots, linkTo(methodOn(this.getClass()).getUserRobots(user)).withSelfRel());
    }

    @PostMapping("/robots/{robotId}/share")
    public ResponseEntity<List<User>> shareRobot(
        @PathVariable int robotId,
        @NotEmpty @RequestParam("users") List<String> users,
        @AuthenticationPrincipal UserEntity currentUser
    ) {
        boolean success = robotRepository.shareRobot(robotId, currentUser.getId(), users);
        if (success) {
            return ResponseEntity.ok(robotRepository.getSharedUsers(robotId, currentUser.getId()));
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/robots/{robotId}/unshare")
    public ResponseEntity<List<User>> unshareRobot(
        @PathVariable int robotId,
        @NotEmpty @RequestParam("users") List<Integer> users,
        @AuthenticationPrincipal UserEntity currentUser
    ) {
        boolean success = robotRepository.unshareRobot(robotId, currentUser.getId(), users);
        if (success) {
            return ResponseEntity.ok(robotRepository.getSharedUsers(robotId, currentUser.getId()));
        }
        return ResponseEntity.badRequest().build();
    }

    @Deprecated
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
