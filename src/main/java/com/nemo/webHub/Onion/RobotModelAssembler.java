package com.nemo.webHub.Onion;

import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.UserEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RobotModelAssembler implements RepresentationModelAssembler<RobotEntity, EntityModel<RobotEntity>> {
    @Override
    public EntityModel<RobotEntity> toModel(RobotEntity robot) {

        return EntityModel.of(robot,
                linkTo(methodOn(RobotAPIController.class).getRobotById(robot.getId(), null)).withSelfRel(),
                linkTo(methodOn(RobotAPIController.class).getUserRobots(null)).withRel("robots"),
                linkTo(methodOn(UserApiController.class).getUserById(robot.getOwnerId())).withRel("owner"));
    }
}
