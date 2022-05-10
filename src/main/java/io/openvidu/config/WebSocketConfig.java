package io.openvidu.config;

import io.openvidu.mvc.controller.InviteMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * HttpSessionHandshakeInterceptor : WebSocket Session에 HttpSession 정보도 추가해줌!!
 */


@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final InviteMessageHandler inviteMessageHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(inviteMessageHandler, "/echo")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*").withSockJS();
    }

}
