package com.techknife.customerportal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "support_tickets")
public class SupportTicket {

    @Id
    private String id;

    @Indexed(unique = true)
    private String ticketNumber;

    @Indexed
    private String customerAccountId;

    private String customerName;

    private String customerEmail;

    @Indexed
    private String projectId;

    private String projectName;

    private String title;

    private String description;

    @Builder.Default
    private String category = "GENERAL"; // TECHNICAL, BILLING, FEATURE_REQUEST, GENERAL, BUG

    @Builder.Default
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    @Builder.Default
    private String status = "OPEN"; // OPEN, IN_PROGRESS, WAITING_ON_CUSTOMER, RESOLVED, CLOSED

    private String assignedToId;

    private String assignedToName;

    private String resolutionNotes;

    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    private Instant closedAt;

    private Instant reopenedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attachment {
        private String fileName;
        private String fileUrl;
        private String publicId;
        private Long fileSize;
        private String contentType;
    }
}
