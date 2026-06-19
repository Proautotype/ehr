package com.custard.ehr.websocket.infrastruture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    @Value("${ws.endpoint:/websocket}")
    private String endpoint;

    @Value("${ws.broker.prefixes:/topic,/queue}")
    private String[] brokerPrefixes;

    @Value("${ws.app.prefix:/app}")
    private String appPrefix;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("websocket endpoint registered {}", endpoint);
        registry
                .addEndpoint(endpoint)
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(brokerPrefixes);
        config.setApplicationDestinationPrefixes(appPrefix);
    }

}
