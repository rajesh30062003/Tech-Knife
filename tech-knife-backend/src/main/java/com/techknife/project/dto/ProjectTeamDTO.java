package com.techknife.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTeamDTO {

    private String teamId;

    @NotBlank(message = "Team name is required")
    private String teamName;

    private String leadEmployeeId;

    private String leadEmployeeName;

    @Builder.Default
    private List<String> memberIds = new ArrayList<>();
}
