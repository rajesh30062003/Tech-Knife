package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "project_status_histories")
public class ProjectStatusHistory {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private ProjectStatus oldStatus;

    private ProjectStatus newStatus;

    private String changedBy;

    private String changedByName;

    private String reason;

    @CreatedDate
    private Instant changedAt;

    @CreatedBy
    private String createdBy;
}
