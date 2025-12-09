package com.system.hakeem.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.allowed.origins:*}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // for broadcasting locations
        registry.setApplicationDestinationPrefixes("/app"); // for client -> server messages
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] origins = allowedOrigins.equals("*") ? new String[] { "*" } : allowedOrigins.split(",");
        registry.addEndpoint("/ws") // endpoint for websocket handshake
                .setAllowedOriginPatterns(origins) // configurable allowed origins
                .withSockJS(); // fallback if websocket not supported
    }
}
