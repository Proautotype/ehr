package com.custard.ehr.websocket.presentation;

import com.custard.ehr.shared.infrastruture.config.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    private final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final String BROKER_PREFIX = "/topic/%s";
    private final SimpMessagingTemplate messaging;

    public MessageController(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }


    @MessageMapping("/ping")
    @SendTo("/topic/messages")
    public String sendMessage(String text) {
        logger.info("Message {}",text);
        messaging.convertAndSend(
                String.format(BROKER_PREFIX, "LAB_ORDER_CREATED"),
                "{'data':'hello'}"
        );
        return "Server received: " + text;
    }

}
