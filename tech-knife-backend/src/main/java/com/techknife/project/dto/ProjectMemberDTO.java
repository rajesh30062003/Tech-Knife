package com.techknife.project.dto;

import com.techknife.project.entity.ProjectMemberRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDTO {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String employeeName;

    @Builder.Default
    private ProjectMemberRole role = ProjectMemberRole.MEMBER;

    @Builder.Default
    private Double allocationPercentage = 100.0;

    private LocalDate joinedDate;
}
