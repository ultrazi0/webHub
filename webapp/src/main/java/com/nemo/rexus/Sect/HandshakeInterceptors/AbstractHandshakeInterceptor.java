package com.nemo.rexus.Sect.HandshakeInterceptors;

import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Decibel.RobotRepository;
import com.nemo.rexus.Decibel.UserEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Abstract parent for Handshake Interceptors. It overrides the {@link HandshakeInterceptor#afterHandshake}
 * method to do nothing, because there is no need to do anything in all subclasses. Furthermore,
 * it provides some methods for convenience.
 * */
public abstract class AbstractHandshakeInterceptor implements HandshakeInterceptor {

    protected final RobotRepository robotRepository;

    public AbstractHandshakeInterceptor() {
        this.robotRepository = null;
    }
    
    public AbstractHandshakeInterceptor(RobotRepository robotRepository) {
        this.robotRepository = robotRepository;
    }

    @Override
    public abstract boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                            @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes)
            throws Exception;

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler, Exception exception) {

    }

    @Nullable
    protected Integer getRobotIdFromRequestOrElseNull(@NotNull ServerHttpRequest request) {

        String path = request.getURI().getPath();

        try {
            return Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    protected UserEntity getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserEntity) {
            return (UserEntity) principal;
        }
        throw new IllegalArgumentException(
                "Principal is not an instance of UserEntity, but of " + principal.getClass().getName()
        );
    }

    protected RobotEntity getCurrentRobot() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof RobotEntity) return (RobotEntity) principal;
        throw new IllegalArgumentException(
                "Principal in not an instance of RobotEntity, but of " + principal.getClass().getName()
        );
    }
}
