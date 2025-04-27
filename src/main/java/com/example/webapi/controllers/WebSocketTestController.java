package com.example.webapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketTestController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/test")
    @SendTo("/topic/test")
    public Map<String, String> handleTest(Map<String, String> message) {
        System.out.println("Broadcast message received: " + message.get("content"));
        return message;
    }

    @GetMapping("/api/ws-test/broadcast/{message}")
    @ResponseBody
    public String broadcastMessage(@PathVariable String message) {
        Map<String, String> payload = new HashMap<>();
        payload.put("content", message);
        
        System.out.println("Broadcasting message: " + message);
        messagingTemplate.convertAndSend("/topic/test", payload);
        
        return "Message sent: " + message;
    }

    @GetMapping("/api/ws-test/user/{userId}/{message}")
    @ResponseBody
    public String sendToUser(@PathVariable Long userId, @PathVariable String message) {
        Map<String, String> payload = new HashMap<>();
        payload.put("content", message);
        
        System.out.println("Sending message to user " + userId + ": " + message);
        
        // Cách 1: Sử dụng convertAndSendToUser
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                payload
        );
        
        // Cách 2: Sử dụng đường dẫn đầy đủ (thử cả hai cách)
        messagingTemplate.convertAndSend(
                "/user/" + userId + "/queue/messages",
                payload
        );
        
        System.out.println("Tin nhắn đã được gửi qua cả hai cách");
        
        return "Message sent to user " + userId + ": " + message;
    }
} 