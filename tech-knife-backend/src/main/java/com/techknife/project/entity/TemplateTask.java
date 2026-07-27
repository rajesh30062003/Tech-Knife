package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateTask {
    private String title;
    private String description;
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;
    @Builder.Default
    private Double estimatedHours = 8.0;
    private String milestoneTitle;
}
