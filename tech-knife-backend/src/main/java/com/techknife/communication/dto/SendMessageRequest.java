package com.techknife.communication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    private String threadId;
    private String subject;
    private List<String> recipientIds;

    @NotBlank(message = "Content is required")
    private String content;

    private List<MessageAttachmentDTO> attachments;
}
