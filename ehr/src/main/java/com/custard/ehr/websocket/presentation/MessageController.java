package com.custard.ehr.websocket.presentation;

import com.custard.ehr.websocket.domain.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    private final SimpMessagingTemplate messaging;

    public MessageController(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(String from, String text) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFrom(from);
        chatMessage.setText(text);
        return chatMessage;
    }

}
