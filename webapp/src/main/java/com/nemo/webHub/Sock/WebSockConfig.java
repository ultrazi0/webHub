package com.nemo.webHub.Sock;

import com.nemo.webHub.Decibel.RobotRepository;
import com.nemo.webHub.Robot.RobotService;
import com.nemo.webHub.Sect.HandshakeInterceptors.CommandClientHandshakeInterceptor;
import com.nemo.webHub.Sect.HandshakeInterceptors.CommandRobotHandshakeInterceptor;
import com.nemo.webHub.Sect.HandshakeInterceptors.ImageClientHandshakeInterceptor;
import com.nemo.webHub.Sect.HandshakeInterceptors.ImageRobotHandshakeInterceptor;
import com.nemo.webHub.Sock.Command.CommandClientHandler;
import com.nemo.webHub.Sock.Command.CommandRobotHandler;
import com.nemo.webHub.Sock.Image.ImageClientHandler;
import com.nemo.webHub.Sock.Image.ImageRobotHandler;
import com.nemo.webHub.Sock.Image.ImageSubscribers;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSockConfig implements WebSocketConfigurer {

    private final OperatorController operatorController;
    private final RobotService robotService;
    private final RobotRepository robotRepository;
    private final ImageSubscribers imageSubscribers;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(imageClientHandler(), "/api/image/client/{robotId}")
                .addInterceptors(new ImageClientHandshakeInterceptor(robotRepository)).setAllowedOrigins("*");

        registry.addHandler(imageRobotHandler(), "/api/image/robot")
                .addInterceptors(new ImageRobotHandshakeInterceptor(imageSubscribers));

        registry.addHandler(commandRobotHandler(), "/api/command/robot")
                .addInterceptors(new CommandRobotHandshakeInterceptor(robotService));

        registry.addHandler(commandClientHandler(), "/api/command/client/{robotId}")
                .addInterceptors(new CommandClientHandshakeInterceptor(robotRepository, operatorController)).setAllowedOrigins("*");
    }

    private WebSocketHandler imageClientHandler() {
        return new ImageClientHandler(imageSubscribers);
    }

    private WebSocketHandler imageRobotHandler() {
        return new ImageRobotHandler(imageSubscribers);
    }

    private WebSocketHandler commandRobotHandler() {
        return new CommandRobotHandler(robotService, operatorController);
    }

    private WebSocketHandler commandClientHandler() {
        return new CommandClientHandler(robotService, operatorController, imageSubscribers);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebsocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize((int) Math.pow(2, 20));

        return container;
    }

    @Deprecated
    private static Map<String, String> getQueryParametersMap(String query) {
        Map<String, String> map = new HashMap<>();

        for (String param : query.split("&")) {
            String[] splitParam = param.split("=");

            map.put(splitParam[0], splitParam[1]);
        }

        return map;
    }
}
