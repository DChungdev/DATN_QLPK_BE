package com.example.webapi.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;

    private boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private Date createdAt = new Date();
} 