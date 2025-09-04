package com.nemo.webHub.Sect.HandshakeInterceptors;

import com.nemo.webHub.Decibel.RobotRepository;
import com.nemo.webHub.Decibel.UserEntity;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

/**
 * Handshake Interceptor for connection requests sent from a user to connect to their robot
 * in order to see the robot's stream. In the endpoint the user must specify to which
 * robot they want to connect (otherwise 400). In order to be allowed here, the user must
 * obviously be authenticated, which means that it can be checked whether they are allowed
 * to access this robot (if not 404 is returned). Furthermore, in contrast to
 * {@link CommandClientHandshakeInterceptor}, this interceptor allows for more than one
 * user to connect to a single robot simultaneously, because, unlike with commands, there
 * is no problem with transmitting images to multiple users.
 *
 * @see AbstractHandshakeInterceptor
 * */
public class ImageClientHandshakeInterceptor extends AbstractHandshakeInterceptor {

    public ImageClientHandshakeInterceptor(RobotRepository robotRepository) {
        super(robotRepository);
        Assert.notNull(robotRepository, "database access is necessary - RobotRepository cannot be null");
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {

        Integer robotId = getRobotIdFromRequestOrElseNull(request);

        if (robotId == null) {
            // Wrong or not parsable ID is provided (not integer)
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        // Web browsers send cookies even with websocket connect requests
        UserEntity user = getCurrentUser();

        // If the robot is not found, 404 is returned thanks to the @ResponseStatus annotation on the exception
        RobotsRecord robot = robotRepository.findRobotsRecordByIdIfAllowed(robotId, user.getId());
        Assert.isTrue(robotId.equals(robot.getRobotId()), "IDs do not match");

        // Add ID to the attribute map so that it is easier to access
        attributes.put("robotId", robotId);

        // Allow multiple users to connect to the stream
        return true;
    }
}
