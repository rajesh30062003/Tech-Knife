package com.techknife.employee.dto;

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
public class OrgTreeNode {
    private String id;
    private String employeeId;
    private String fullName;
    private String officialEmail;
    private String designationId;
    private String departmentId;
    private String profileImage;

    @Builder.Default
    private List<OrgTreeNode> children = new ArrayList<>();
}
