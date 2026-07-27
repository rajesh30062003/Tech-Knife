package com.techknife.project.dto;

import com.techknife.project.entity.TemplateMilestone;
import com.techknife.project.entity.TemplateTask;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTemplateDTO {

    private String id;

    @NotBlank(message = "Template code is required")
    private String templateCode;

    @NotBlank(message = "Template name is required")
    private String name;

    private String description;

    @Builder.Default
    private List<String> technologyStack = new ArrayList<>();

    @Builder.Default
    private List<TemplateMilestone> defaultMilestones = new ArrayList<>();

    @Builder.Default
    private List<TemplateTask> defaultTasks = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}
