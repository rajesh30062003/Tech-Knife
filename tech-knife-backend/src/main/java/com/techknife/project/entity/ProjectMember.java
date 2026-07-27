package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {
    private String employeeId;
    private String employeeName;
    @Builder.Default
    private ProjectMemberRole role = ProjectMemberRole.MEMBER;
    @Builder.Default
    private Double allocationPercentage = 100.0;
    private LocalDate joinedDate;
}
