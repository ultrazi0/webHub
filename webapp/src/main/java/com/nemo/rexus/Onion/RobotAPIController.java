package com.nemo.rexus.Onion;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nemo.rexus.Commands.CommandService;
import com.nemo.rexus.Commands.CommandType;
import com.nemo.rexus.Decibel.RobotService;
import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Onion.Converters.CommandTypeDeserializer;
import com.nemo.rexus.User.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/robots")
@RequiredArgsConstructor
public class RobotAPIController {

    private final RobotService robotService;
    private final RobotModelAssembler robotModelAssembler;
    private final CommandService commandService;

    @GetMapping("{robotId}/commands")
    public CommandType[] getRobotCommands(@PathVariable int robotId) {
        return commandService.getAllCommandForRobot(robotId);
    }

    @PostMapping("{robotId}/commands")
    public CommandType insertRobotCommand(
        @PathVariable int robotId,
        @RequestParam("commandType") @NotEmpty String commandTypeString,
        @RequestParam("commandKeys") String[] commandKeys
    ) {
        return commandService.insertOrUpdateCustomCommand(robotId, commandTypeString, commandKeys);
    }

    @DeleteMapping("{robotId}/commands")
    public ResponseEntity<Void> deleteRobotCommand(
        @PathVariable int robotId,
        @RequestParam("commandType") @NotEmpty String commandTypeString
    ) {
        if (commandService.deleteCustomCommand(robotId, commandTypeString)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("{robotId}")
    public EntityModel<RobotEntity> getRobotById(@PathVariable int robotId, @AuthenticationPrincipal UserEntity user) {
        RobotEntity robot = robotService.getRobotById(robotId, user.getId());

        return robotModelAssembler.toModel(robot);
    }

    @PostMapping
    public ResponseEntity<EntityModel<RobotEntity>> insertNewRobot(
            @NotBlank @RequestParam("name") String name, @AuthenticationPrincipal UserEntity user) {

        EntityModel<RobotEntity> robotEntityModel = robotModelAssembler
                .toModel(robotService.insertRobot(name, user.getId()));

        return ResponseEntity
                .created(robotEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(robotEntityModel);
    }

    @PutMapping("{robotId}")
    public ResponseEntity<EntityModel<RobotEntity>> updateRobot(
            @PathVariable int robotId,
            @Valid @RequestBody EditRobotRequest request,
            @AuthenticationPrincipal UserEntity user
    ) {

        EntityModel<RobotEntity> robotEntityModel = robotModelAssembler.toModel(
                robotService.updateRobot(robotId, request.name(), request.commands(), user.getId())
        );

        return ResponseEntity
                .created(robotEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(robotEntityModel);
    }

    @DeleteMapping("{robotId}")
    public ResponseEntity<Void> deleteRobot(@PathVariable int robotId, @AuthenticationPrincipal UserEntity user) {
        robotService.deleteRobot(robotId, user.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public CollectionModel<EntityModel<RobotEntity>> getUserRobots(@AuthenticationPrincipal UserEntity user) {

        List<EntityModel<RobotEntity>> robots = robotService.getUserRobots(user.getId())
            .map(robotModelAssembler::toModel).toList();

        return CollectionModel.of(robots, linkTo(methodOn(this.getClass()).getUserRobots(user)).withSelfRel());
    }

    @PostMapping("{robotId}/share")
    public ResponseEntity<List<User>> shareRobot(
        @PathVariable int robotId,
        @NotEmpty @RequestParam("users") List<String> users,
        @AuthenticationPrincipal UserEntity currentUser
    ) {
        boolean success = robotService.shareRobot(robotId, currentUser.getId(), users);
        if (success) {
            return ResponseEntity.ok(robotService.getSharedUsers(robotId, currentUser.getId()));
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{robotId}/unshare")
    public ResponseEntity<List<User>> unshareRobot(
        @PathVariable int robotId,
        @NotEmpty @RequestParam("users") List<Integer> users,
        @AuthenticationPrincipal UserEntity currentUser
    ) {
        boolean success = robotService.unshareRobot(robotId, currentUser.getId(), users);
        if (success) {
            return ResponseEntity.ok(robotService.getSharedUsers(robotId, currentUser.getId()));
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

    public record EditRobotRequest(
        @NotBlank String name,
        @JsonDeserialize(contentUsing = CommandTypeDeserializer.class) List<CommandType> commands
    ) {}

}
