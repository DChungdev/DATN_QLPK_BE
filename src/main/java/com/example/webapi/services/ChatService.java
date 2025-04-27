package com.example.webapi.services;

import com.example.webapi.models.dto.ChatMessageDTO;
import com.example.webapi.models.entities.Account;
import com.example.webapi.models.entities.ChatMessage;
import com.example.webapi.repositories.AccountRepository;
import com.example.webapi.repositories.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private AccountRepository accountRepository;

    public ChatMessageDTO saveMessage(ChatMessageDTO messageDTO) {
        Account sender = accountRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        Account receiver = accountRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        ChatMessage message = ChatMessage.builder()
                .content(messageDTO.getContent())
                .sender(sender)
                .receiver(receiver)
                .isRead(false)
                .createdAt(new Date())
                .build();
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        return mapToDTO(savedMessage);
    }

    public List<ChatMessageDTO> getMessagesBetweenUsers(Long user1Id, Long user2Id) {
        List<ChatMessage> messages = chatMessageRepository.findMessagesBetweenUsers(user1Id, user2Id);
        
        return messages.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ChatMessageDTO> getUnreadMessagesForUser(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findUnreadMessagesForUser(userId);
        
        return messages.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void markMessageAsRead(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        message.setRead(true);
        chatMessageRepository.save(message);
    }

    public void markAllMessagesAsRead(Long senderId, Long receiverId) {
        List<ChatMessage> messages = chatMessageRepository.findMessagesBetweenUsers(senderId, receiverId);
        
        messages.stream()
                .filter(msg -> msg.getReceiver().getAccountId().equals(receiverId) && !msg.isRead())
                .forEach(msg -> {
                    msg.setRead(true);
                    chatMessageRepository.save(msg);
                });
    }

    public List<Long> getChatUserIds(Long userId) {
        List<Long> senders = chatMessageRepository.findDistinctSendersForReceiver(userId);
        List<Long> receivers = chatMessageRepository.findDistinctReceiversForSender(userId);
        
        return Stream.concat(senders.stream(), receivers.stream())
                .distinct()
                .filter(id -> !id.equals(userId))
                .collect(Collectors.toList());
    }

    private ChatMessageDTO mapToDTO(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getAccountId())
                .senderName(message.getSender().getFullName())
                .receiverId(message.getReceiver().getAccountId())
                .receiverName(message.getReceiver().getFullName())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
} 