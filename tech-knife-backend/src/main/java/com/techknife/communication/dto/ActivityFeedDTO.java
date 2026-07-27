package com.techknife.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFeedDTO {

    private String id;
    private String actorId;
    private String actorName;
    private String action;
    private String module;
    private String entityType;
    private String entityId;
    private String entityName;
    private String description;
    private String targetId;
    private Map<String, Object> metadata;
    private Instant createdAt;
}
