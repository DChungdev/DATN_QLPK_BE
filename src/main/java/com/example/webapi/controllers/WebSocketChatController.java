package com.example.webapi.controllers;

import com.example.webapi.models.dto.ChatMessageDTO;
import com.example.webapi.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void handleChatMessage(@Payload ChatMessageDTO messageDTO) {
        // Save message to database
        ChatMessageDTO savedMessage = chatService.saveMessage(messageDTO);
        System.out.println("Sắp gửi tin nhắn đến user: " + savedMessage.getReceiverId());
        // Send to specific user queue
        messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiverId().toString(),
                "/queue/messages",
                savedMessage
        );
        System.out.println("Đã gửi tin nhắn đến user: " + savedMessage.getReceiverId());
    }

    @MessageMapping("/chat.markRead")
    public void handleMarkRead(@Payload Long messageId) {
        chatService.markMessageAsRead(messageId);
    }
} 