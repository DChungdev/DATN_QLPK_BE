package com.example.webapi.repositories;

import com.example.webapi.models.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT m FROM ChatMessage m WHERE (m.sender.id = :account1Id AND m.receiver.id = :account2Id) OR (m.sender.id = :account2Id AND m.receiver.id = :account1Id) ORDER BY m.createdAt")
    List<ChatMessage> findMessagesBetweenUsers(@Param("account1Id") Long account1Id, @Param("account2Id") Long account2Id);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.receiver.id = :accountId AND m.isRead = false")
    List<ChatMessage> findUnreadMessagesForUser(@Param("accountId") Long accountId);
    
    @Query("SELECT DISTINCT m.sender.id FROM ChatMessage m WHERE m.receiver.id = :accountId")
    List<Long> findDistinctSendersForReceiver(@Param("accountId") Long accountId);
    
    @Query("SELECT DISTINCT m.receiver.id FROM ChatMessage m WHERE m.sender.id = :accountId")
    List<Long> findDistinctReceiversForSender(@Param("accountId") Long accountId);
} 