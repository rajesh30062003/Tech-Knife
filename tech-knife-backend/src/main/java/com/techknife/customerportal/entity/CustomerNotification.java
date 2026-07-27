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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_notifications")
public class CustomerNotification {

    @Id
    private String id;

    @Indexed
    private String customerAccountId;

    private String title;

    private String message;

    private String type; // PROJECT_UPDATE, MILESTONE_COMPLETED, TICKET_REPLY, INVOICE_GENERATED, PAYMENT_RECEIVED, ANNOUNCEMENT

    @Builder.Default
    private Boolean isRead = false;

    private String linkUrl;

    @CreatedDate
    private Instant createdAt;
}
