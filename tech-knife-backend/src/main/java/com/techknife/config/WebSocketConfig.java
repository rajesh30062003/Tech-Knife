package com.techknife.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.info("==== [WebSocketTrace] Configuring MessageBroker: simpleBroker='/topic, /queue', applicationPrefix='/app' ====");
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("==== [WebSocketTrace] Registering endpoint: /ws-chat | SockJS enabled: true | Allowed origin patterns: '*' ====");
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*");

        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new org.springframework.messaging.support.ChannelInterceptor() {
            @Override
            public org.springframework.messaging.Message<?> preSend(org.springframework.messaging.Message<?> message, org.springframework.messaging.MessageChannel channel) {
                org.springframework.messaging.simp.stomp.StompHeaderAccessor accessor = org.springframework.messaging.simp.stomp.StompHeaderAccessor.wrap(message);
                log.info("==== [STOMP Inbound] Command='{}', SessionId='{}', Destination='{}' ====",
                        accessor.getCommand(), accessor.getSessionId(), accessor.getDestination());
                return message;
            }
        });
    }

    @Override
    public void configureClientOutboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new org.springframework.messaging.support.ChannelInterceptor() {
            @Override
            public org.springframework.messaging.Message<?> preSend(org.springframework.messaging.Message<?> message, org.springframework.messaging.MessageChannel channel) {
                org.springframework.messaging.simp.stomp.StompHeaderAccessor accessor = org.springframework.messaging.simp.stomp.StompHeaderAccessor.wrap(message);
                log.info("==== [STOMP Outbound] Command='{}', SessionId='{}', Destination='{}', SubscriptionId='{}' ====",
                        accessor.getCommand(), accessor.getSessionId(), accessor.getDestination(), accessor.getSubscriptionId());
                return message;
            }
        });
    }

    @org.springframework.context.event.EventListener
    public void handleSessionConnected(org.springframework.web.socket.messaging.SessionConnectedEvent event) {
        org.springframework.messaging.simp.stomp.StompHeaderAccessor accessor = org.springframework.messaging.simp.stomp.StompHeaderAccessor.wrap(event.getMessage());
        log.info("==== [SessionConnectedEvent] SessionId='{}' ====", accessor.getSessionId());
    }

    @org.springframework.context.event.EventListener
    public void handleSessionSubscribe(org.springframework.web.socket.messaging.SessionSubscribeEvent event) {
        org.springframework.messaging.simp.stomp.StompHeaderAccessor accessor = org.springframework.messaging.simp.stomp.StompHeaderAccessor.wrap(event.getMessage());
        log.info("==== [SessionSubscribeEvent] SessionId='{}', Destination='{}', SubscriptionId='{}' ====",
                accessor.getSessionId(), accessor.getDestination(), accessor.getSubscriptionId());
    }

    @org.springframework.context.event.EventListener
    public void handleSessionDisconnect(org.springframework.web.socket.messaging.SessionDisconnectEvent event) {
        log.info("==== [SessionDisconnectEvent] SessionId='{}', CloseStatus='{}' ====", event.getSessionId(), event.getCloseStatus());
    }
}
