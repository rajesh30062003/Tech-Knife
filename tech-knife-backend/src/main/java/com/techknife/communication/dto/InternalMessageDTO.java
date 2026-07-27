package com.techknife.communication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalMessageDTO {

    private String id;
    private String threadId;
    private String senderId;
    private String senderName;
    private List<String> recipientIds;

    @NotBlank(message = "Content is required")
    private String content;

    private List<MessageAttachmentDTO> attachments;
    private Map<String, Instant> readBy;
    private Instant sentAt;
    private String createdBy;
}
