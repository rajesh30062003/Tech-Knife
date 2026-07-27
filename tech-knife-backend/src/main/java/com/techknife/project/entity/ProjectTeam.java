package com.techknife.project.entity;

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
public class ProjectTeam {
    private String teamId;
    private String teamName;
    private String leadEmployeeId;
    private String leadEmployeeName;
    @Builder.Default
    private List<String> memberIds = new ArrayList<>();
}
