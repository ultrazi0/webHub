package com.nemo.rexus.Sect.HandshakeInterceptors;

import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Sock.Image.ImageSubscribers;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

/**
 * Handshake interceptor for connection requests sent from robots to the image websocket.
 * In order to access this interceptor, the robot must already be authenticated,
 * otherwise Spring Security just won't allow it here. Hence, the ID can be
 * retrieved from the principal object and additional ventures to the database
 * in order to check whether the robot exists is unnecessary.
 *
 * @see AbstractHandshakeInterceptor
 * */
public class ImageRobotHandshakeInterceptor extends AbstractHandshakeInterceptor{

    private final ImageSubscribers imageSubscribers;
    
    public ImageRobotHandshakeInterceptor(ImageSubscribers imageSubscribers) {
        this.imageSubscribers = imageSubscribers;
    }

    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {

        RobotEntity robot = getCurrentRobot();
        
        if (imageSubscribers.robotIsConnected(robot.getId())) {
            // Robot with this ID is already connected - deny the request
            response.setStatusCode(HttpStatus.CONFLICT);
            return false;
        }
        
        attributes.put("robotId", robot.getId());
        
        // Allow connection
        return true;
    }
}
