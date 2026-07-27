package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "project_templates")
public class ProjectTemplate {

    @Id
    private String id;

    @Indexed(unique = true)
    private String templateCode;

    private String name;

    private String description;

    @Builder.Default
    private List<String> technologyStack = new ArrayList<>();

    @Builder.Default
    private List<TemplateMilestone> defaultMilestones = new ArrayList<>();

    @Builder.Default
    private List<TemplateTask> defaultTasks = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;
}
