package com.techknife.communication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communication_internal_messages")
public class InternalMessage {

    @Id
    private String id;

    private String threadId;
    private String senderId;
    private String senderName;
    private List<String> recipientIds;
    private String content;
    private List<MessageAttachment> attachments;
    private Map<String, Instant> readBy; // userId -> readAt

    @CreatedDate
    private Instant sentAt;

    @CreatedBy
    private String createdBy;
}
