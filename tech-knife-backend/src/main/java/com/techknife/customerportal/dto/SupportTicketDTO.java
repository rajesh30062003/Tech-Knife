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
public class SupportTicketDTO {

    private String id;
    private String ticketNumber;
    private String customerAccountId;
    private String customerName;
    private String customerEmail;
    private String projectId;
    private String projectName;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String category;
    private String priority;
    private String status;
    private String assignedToId;
    private String assignedToName;
    private String resolutionNotes;
    private List<SupportTicket.Attachment> attachments;
    private List<TicketReplyDTO> replies;
    private Instant closedAt;
    private Instant reopenedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
