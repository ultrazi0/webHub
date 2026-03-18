package com.nemo.rexus.Sect.HandshakeInterceptors;

import com.nemo.rexus.Decibel.RobotRepository;
import com.nemo.rexus.Decibel.UserEntity;
import com.nemo.rexus.Sock.OperatorController;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

/**
 * Handshake Interceptor for connection requests sent from a user to connect to their robot
 * in order to control (send commands) to it. In the endpoint the user must specify to which
 * robot they want to connect (otherwise 400). In order to be allowed here, the user must
 * obviously be authenticated, which means that it can be checked whether they are allowed
 * to access this robot (if not 404 is returned). Furthermore, only one user at a time can
 * send commands to a specific robot. This means that even if the user is allowed to access it,
 * they are turned down (409 is returned) if someone else already controls this robot.
 * 
 * @see AbstractHandshakeInterceptor
 * */
public class CommandClientHandshakeInterceptor extends AbstractHandshakeInterceptor {

    private final OperatorController operatorController;

    public CommandClientHandshakeInterceptor(RobotRepository robotRepository, OperatorController operatorController) {
        super(robotRepository);
        Assert.notNull(robotRepository, "database access is necessary - RobotRepository cannot be null");
        this.operatorController = operatorController;
    }

    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {

        Integer robotId = getRobotIdFromRequestOrElseNull(request);

        if (robotId == null) {
            // Wrong or not parsable ID is provided (not integer)
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        // Web browsers send cookies even with websocket connect requests
        UserEntity user = getCurrentUser();

        // If the robot is not found, 404 is returned thanks to the @ResponseStatus annotation on the exception
        int allowedRobotId = robotRepository.findRobotIdByRobotIdAndUserIdIfAllowed(robotId, user.getId());
        Assert.isTrue(robotId.equals(allowedRobotId), "IDs must match");

        if (operatorController.getOperatorSessionId(robotId) != null) {
            // Deny the request if someone else is already controlling this robot
            response.setStatusCode(HttpStatus.CONFLICT);
            return false;
        }

        // Add ID to the attribute map so that it is easier to access
        attributes.put("robotId", robotId);

        return true;
    }
}
