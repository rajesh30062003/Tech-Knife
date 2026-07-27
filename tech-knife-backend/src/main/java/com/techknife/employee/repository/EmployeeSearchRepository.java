package com.techknife.employee.repository;

import com.techknife.employee.entity.BloodGroup;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.EmploymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Custom search repository interface for dynamic multi-criteria filtering on {@link Employee} records.
 */
public interface EmployeeSearchRepository {

    /**
     * Dynamically searches and filters employee records based on multiple filter criteria.
     *
     * @param searchTerm Keyword search for matching first name, last name, employee ID, official email, or GitHub username
     * @param departmentId Filter by department identifier
     * @param designationId Filter by designation identifier
     * @param managerId Filter by direct manager identifier
     * @param status Filter by employee status
     * @param employmentType Filter by employment classification type
     * @param bloodGroup Filter by blood group
     * @param skills Filter by list of required skills (must match all)
     * @param pageable Pagination and sorting configuration
     * @return Paginated result containing matching {@link Employee} records
     */
    Page<Employee> searchEmployees(
            String searchTerm,
            String departmentId,
            String designationId,
            String managerId,
            EmployeeStatus status,
            EmploymentType employmentType,
            BloodGroup bloodGroup,
            List<String> skills,
            Pageable pageable
    );
}

