package com.techknife.employee.dto;

import com.techknife.employee.entity.EmployeeStatus;
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
public class OrgTreeNodeResponse {
    private String id;
    private String employeeId;
    private String fullName;
    private String designationId;
    private String departmentId;
    private EmployeeStatus status;
    private String profileImage;

    @Builder.Default
    private List<OrgTreeNodeResponse> subordinates = new ArrayList<>();
}
