package com.example.webapi.models.dto;

import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long id;
    private String content;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private boolean isRead;
    private Date createdAt;
} 