package com.techknife.project.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class ProjectAssignDTO {
    private String projectManagerId;
    private String projectLeadId;

    @JsonAlias({"employeeIds", "assignedEmployeeIds"})
    @Builder.Default
    private List<String> assignedEmployees = new ArrayList<>();

    @JsonAlias({"internIds", "assignedInternIds"})
    @Builder.Default
    private List<String> assignedInterns = new ArrayList<>();

    public List<String> getAssignedEmployees() {
        return assignedEmployees != null ? assignedEmployees : new ArrayList<>();
    }

    public List<String> getAssignedInterns() {
        return assignedInterns != null ? assignedInterns : new ArrayList<>();
    }
}
