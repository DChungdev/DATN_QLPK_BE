package com.example.webapi.controllers;

import com.example.webapi.models.dto.ChatMessageDTO;
import com.example.webapi.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody ChatMessageDTO messageDTO) {
        ChatMessageDTO savedMessage = chatService.saveMessage(messageDTO);
        
        // Send message to specific user via WebSocket
        messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiverId().toString(),
                "/queue/messages",
                savedMessage
        );
        
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/history/{user1Id}/{user2Id}")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(
            @PathVariable Long user1Id,
            @PathVariable Long user2Id) {
        
        List<ChatMessageDTO> messages = chatService.getMessagesBetweenUsers(user1Id, user2Id);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getUnreadMessages(@PathVariable Long userId) {
        List<ChatMessageDTO> unreadMessages = chatService.getUnreadMessagesForUser(userId);
        return ResponseEntity.ok(unreadMessages);
    }

    @PutMapping("/read/{messageId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId) {
        chatService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all/{senderId}/{receiverId}")
    public ResponseEntity<Void> markAllAsRead(
            @PathVariable Long senderId,
            @PathVariable Long receiverId) {
        
        chatService.markAllMessagesAsRead(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Long>> getChatUsers(@PathVariable Long userId) {
        List<Long> userIds = chatService.getChatUserIds(userId);
        return ResponseEntity.ok(userIds);
    }
} 