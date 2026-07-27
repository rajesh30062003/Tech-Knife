package com.techknife.communication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "communication_activity_feeds")
public class ActivityFeed {

    @Id
    private String id;

    private String actorId;
    private String actorName;
    private String action; // CREATE, UPDATE, DELETE, SEND, PUBLISH, READ
    private String module;
    private String entityType;
    private String entityId;
    private String entityName;
    private String description;
    private String targetId;
    private Map<String, Object> metadata;

    @CreatedDate
    private Instant createdAt;
}
