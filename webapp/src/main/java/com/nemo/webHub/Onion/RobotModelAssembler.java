package com.nemo.webHub.Onion;

import com.nemo.webHub.Decibel.RobotEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RobotModelAssembler implements RepresentationModelAssembler<RobotEntity, EntityModel<RobotEntity>> {
    @NonNull
    @Override
    public EntityModel<RobotEntity> toModel(@NonNull RobotEntity robot) {
        /*
        * Because methodOn() requires only a reference to create a link, it does not matter what
        * is passed as arguments (except, of course, when an argument is annotated with @PathVariable).
        * This is why passing null is absolutely safe: the uri does not depend on the current user.
        */

        return EntityModel.of(robot,
                linkTo(methodOn(RobotAPIController.class).getRobotById(robot.getId(), null)).withSelfRel(),
                linkTo(methodOn(RobotAPIController.class).getUserRobots(null)).withRel("robots"),
                linkTo(methodOn(UserApiController.class).getUserById(robot.getOwner().getId())).withRel("owner"));
    }
}
