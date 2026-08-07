package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "planning_documents")
public class PlanningDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String projectId;

    private String title;

    private String category;

    private String content;

    @Builder.Default
    private String editorType = "RICH_TEXT";

    private String diagramJson;

    private String createdBy;

    private String createdByRole;

    private String updatedBy;

    private String updatedByRole;

    private Instant createdAt;

    private Instant updatedAt;

    @Builder.Default
    private Integer version = 1;

    @Builder.Default
    private Boolean isLocked = false;

    private String lockedBy;

    private Instant lockedAt;
}
