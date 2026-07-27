package com.techknife.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageThreadDTO {

    private String id;
    private String subject;
    private List<String> participantIds;
    private String lastMessage;
    private Instant lastMessageAt;
    private boolean isGroup;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
}
