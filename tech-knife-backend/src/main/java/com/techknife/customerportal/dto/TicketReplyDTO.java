package com.techknife.customerportal.dto;

import com.techknife.customerportal.entity.SupportTicket;
import jakarta.validation.constraints.NotBlank;
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
public class TicketReplyDTO {

    private String id;
    private String ticketId;
    private String senderType;
    private String senderId;
    private String senderName;
    private String senderEmail;

    @NotBlank(message = "Reply message cannot be empty")
    private String message;

    private List<SupportTicket.Attachment> attachments;
    private Instant createdAt;
}
