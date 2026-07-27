package com.techknife.customerportal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ticket_replies")
public class TicketReply {

    @Id
    private String id;

    @Indexed
    private String ticketId;

    private String senderType; // CUSTOMER, AGENT, SYSTEM

    private String senderId;

    private String senderName;

    private String senderEmail;

    private String message;

    @Builder.Default
    private List<SupportTicket.Attachment> attachments = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;
}
