package com.techknife.project.dto;

import com.techknife.project.entity.ProjectLinks;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectLinksUpdateDTO {
    private ProjectLinks links;
    private String repositoryVisibility;
    private String deploymentType;
}
