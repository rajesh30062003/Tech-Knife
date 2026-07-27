package com.techknife.employee.dto;

import com.techknife.employee.entity.BloodGroup;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Filter Data Transfer Object encapsulating search parameters for employee query endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSearchFilter {

    private String searchTerm;
    private String companyId;
    private String branchId;
    private String departmentId;
    private String designationId;
    private String teamId;
    private String managerId;
    private EmployeeStatus status;
    private EmploymentType employmentType;
    private BloodGroup bloodGroup;
    private List<String> skills;
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;
    @Builder.Default
    private String sortBy = "createdAt";
    @Builder.Default
    private String sortDirection = "DESC";
}
