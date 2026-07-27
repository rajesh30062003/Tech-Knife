package com.techknife.crm.entity;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "crm_communication_logs")
public class CommunicationLog {

    @Id
    private String id;

    private String entityType; // LEAD, CUSTOMER, OPPORTUNITY

    @Indexed
    private String entityId;

    private String type; // EMAIL, CALL, MEETING, NOTE, WHATSAPP, SMS

    private String direction; // INBOUND, OUTBOUND

    private String subject;

    private String content;

    private String conductedBy;

    private String conductedByName;

    private Instant timestamp;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
