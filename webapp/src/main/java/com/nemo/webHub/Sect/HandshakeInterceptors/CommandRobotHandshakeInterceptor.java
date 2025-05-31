package com.nemo.webHub.Sect.HandshakeInterceptors;

import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Robot.RobotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

/**
 * Handshake interceptor for connection requests sent from robots to the command websocket.
 * In order to access this interceptor, the robot must already be authenticated;
 * otherwise, Spring Security just won't allow it here. Hence, the ID can be
 * retrieved from the principal object and additional ventures to the database
 * in order to check whether the robot exists is unnecessary.
 *
 * @see AbstractHandshakeInterceptor
 * */
public class CommandRobotHandshakeInterceptor extends AbstractHandshakeInterceptor{

    private final RobotService robotService;

    public CommandRobotHandshakeInterceptor(RobotService robotService) {
        this.robotService = robotService;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes
    ) {

        RobotEntity robot = getCurrentRobot();

        if (robotService.robotIsConnected(robot.getId())) {
            // Another robot with this ID is already connected - deny request
            response.setStatusCode(HttpStatus.CONFLICT);
            return false;
        }

        attributes.put("robotId", robot.getId());

        // Allow connection
        return true;
    }
}
