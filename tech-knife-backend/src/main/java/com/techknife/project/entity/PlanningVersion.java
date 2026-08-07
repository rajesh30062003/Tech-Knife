package com.techknife.project.entity;

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
@Document(collection = "planning_versions")
public class PlanningVersion {

    @Id
    private String id;

    @Indexed
    private String projectId;

    @Indexed
    private String documentId;

    private Integer versionNumber;

    private String savedBy;

    private String savedByRole;

    @CreatedDate
    private Instant savedAt;

    private String docTitle;

    private String category;

    private String content;

    private String diagramJson;
}
