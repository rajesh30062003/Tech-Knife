package com.techknife.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationLogDTO {
    private String id;
    private String entityType;
    private String entityId;
    private String type;
    private String direction;
    private String subject;
    private String content;
    private String conductedBy;
    private String conductedByName;
    private Instant timestamp;
    private Instant createdAt;
    private Instant updatedAt;
}
