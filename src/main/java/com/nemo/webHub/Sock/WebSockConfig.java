package com.nemo.webHub.Sock;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.nemo.webHub.Robot.RobotService;
import com.nemo.webHub.Sock.Command.CommandClientHandler;
import com.nemo.webHub.Sock.Command.CommandRobotHandler;
import com.nemo.webHub.Sock.Image.ImageRobotHandler;
import com.nemo.webHub.Sock.Image.ImageClientHandler;
import com.nemo.webHub.Sock.Image.ImageSubscribers;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSockConfig implements WebSocketConfigurer {

    @Autowired
    private Operators operators;
    @Autowired
    private RobotService robotService;
    @Autowired
    private ImageSubscribers imageSubscribers;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(imageClientHandler(), "/api/image/client/{robotId}")
                .addInterceptors(imageClientHandshakeInterceptor()).setAllowedOrigins("*");

        registry.addHandler(imageRobotHandler(), "/api/image/robot/{robotId}")
                .addInterceptors(imageRobotHandshakeInterceptor());

        registry.addHandler(commandRobotHandler(), "/api/command/robot/{robotId}")
                .addInterceptors(commandRobotHandshakeInterceptor());

        registry.addHandler(commandClientHandler(), "/api/command/client/{robotId}")
                .addInterceptors(commandClientHandshakeInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler imageClientHandler() {
        return new ImageClientHandler();
    }

    @Bean
    public WebSocketHandler imageRobotHandler() {
        return new ImageRobotHandler();
    }

    @Bean
    public WebSocketHandler commandRobotHandler() {
        return new CommandRobotHandler();
    }

    @Bean
    public WebSocketHandler commandClientHandler() {
        return new CommandClientHandler();
    }

    private HandshakeInterceptor commandClientHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request,
                                           ServerHttpResponse response,
                                           WebSocketHandler wsHandler,
                                           Map<String, Object> attributes) throws Exception {

                Integer robotId = getRobotIdFromRequest(request);

                if ((robotId == null) || (operators.getOperatorSessionId(robotId) != null)) {
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    return false;
                }

                attributes.put("robotId", robotId);

                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request,
                                       ServerHttpResponse response,
                                       WebSocketHandler wsHandler,
                                       Exception exception) {}
        };
    }

    private HandshakeInterceptor commandRobotHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request,
                                           ServerHttpResponse response,
                                           WebSocketHandler wsHandler,
                                           Map<String, Object> attributes) throws Exception {
                Integer robotId = getRobotIdFromRequest(request);

                if ((robotId == null) || (robotService.robotIsConnected(robotId))) {
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    return false;
                }

                attributes.put("robotId", robotId);

                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request,
                                       ServerHttpResponse response,
                                       WebSocketHandler wsHandler,
                                       Exception exception) {}
        };
    }

    private HandshakeInterceptor imageClientHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request,
                                           ServerHttpResponse response,
                                           WebSocketHandler wsHandler,
                                           Map<String, Object> attributes) throws Exception {
                Integer robotId = getRobotIdFromRequest(request);

                if (robotId == null) {
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    return false;
                }

                attributes.put("robotId", robotId);

                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request,
                                       ServerHttpResponse response,
                                       WebSocketHandler wsHandler,
                                       Exception exception) {}
        };
    }

    private HandshakeInterceptor imageRobotHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request,
                                           ServerHttpResponse response,
                                           WebSocketHandler wsHandler,
                                           Map<String, Object> attributes) throws Exception {
                Integer robotId = getRobotIdFromRequest(request);

                if ((robotId == null) || (imageSubscribers.robotIsConnected(robotId))) {
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    return false;
                }

                attributes.put("robotId", robotId);

                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request,
                                       ServerHttpResponse response,
                                       WebSocketHandler wsHandler,
                                       Exception exception) {}
        };
    }

    @Bean
    public ServletServerContainerFactoryBean createWebsocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize((int) Math.pow(2, 20));

        return container;
    }

    public static TextMessage createRegularJsonTextMessage(@NotNull CharSequence message) throws IOException {
        // See https://www.baeldung.com/jackson-streaming-api

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(stream, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("messageType", "regularMessage");
        jsonGenerator.writeStringField("message", message.toString());
        jsonGenerator.writeEndObject();

        jsonGenerator.close();

        return new TextMessage(stream.toString(StandardCharsets.UTF_8));
    }

    @Nullable
    private Integer getRobotIdFromRequest(@NotNull ServerHttpRequest request) {
        Integer robotId = null;

        String path = request.getURI().getPath();
        try {
            robotId = Integer.valueOf(path.substring(path.lastIndexOf('/') + 1));
        } catch (NumberFormatException ignored) {
            System.out.println("Connection requested without a valid ID parameter - connection refused");
        }

        return robotId;
    }
}
