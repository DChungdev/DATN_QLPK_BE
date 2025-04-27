package com.example.webapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ws-debug")
public class WebSocketDebugController {

    @Autowired
    private SimpUserRegistry userRegistry;

    @GetMapping("/users")
    public Map<String, Object> getConnectedUsers() {
        Map<String, Object> result = new HashMap<>();
        result.put("connectedUsers", userRegistry.getUserCount());
        result.put("userNames", userRegistry.getUsers().stream()
            .map(user -> user.getName())
            .toList());
        return result;
    }

    @GetMapping("/status")
    public Map<String, Object> getWebSocketStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("active", true);
        status.put("connectedUsers", userRegistry.getUserCount());
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }
} 