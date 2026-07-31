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
@Document(collection = "projectActivities")
public class ProjectActivity {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private String action;

    private String performedBy;

    private String userRole;

    private String fieldModified;

    private String oldValue;

    private String newValue;

    @CreatedDate
    private Instant timestamp;
}
