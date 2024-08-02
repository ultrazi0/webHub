package com.nemo.webHub.Onion;

import com.nemo.webHub.Decibel.RobotEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RobotModelAssembler implements RepresentationModelAssembler<RobotEntity, EntityModel<RobotEntity>> {
    @Override
    public EntityModel<RobotEntity> toModel(RobotEntity robot) {

        return EntityModel.of(robot,
                linkTo(methodOn(OnionAPIController.class).getRobotById(robot.getId())).withSelfRel(),
                linkTo(methodOn(OnionAPIController.class).getAllRobots()).withRel("robots"));
    }
}
