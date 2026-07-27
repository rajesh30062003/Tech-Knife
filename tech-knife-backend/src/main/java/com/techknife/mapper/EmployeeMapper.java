package com.techknife.mapper;

import com.techknife.dto.CreateEmployeeRequest;
import com.techknife.dto.EmployeeDTO;
import com.techknife.dto.EmployeeResponse;
import com.techknife.dto.UpdateEmployeeRequest;
import com.techknife.entity.Employee;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component("comTechknifeEmployeeMapper")
public class EmployeeMapper {

    public Employee toEntity(CreateEmployeeRequest request) {
        if (request == null) {
            return null;
        }

        return Employee.builder()
                .employeeId(request.getEmployeeId())
                .officialEmail(request.getOfficialEmail())
                .personalEmail(request.getPersonalEmail())
                .primaryMobile(request.getPrimaryMobile())
                .alternateMobile(request.getAlternateMobile())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .dob(request.getDob())
                .bloodGroup(request.getBloodGroup())
                .departmentId(request.getDepartmentId())
                .designationId(request.getDesignationId())
                .managerId(request.getManagerId())
                .joiningDate(request.getJoiningDate())
                .employmentType(request.getEmploymentType())
                .salary(request.getSalary())
                .skills(request.getSkills() != null ? new ArrayList<>(request.getSkills()) : new ArrayList<>())
                .githubUsername(request.getGithubUsername())
                .profileImage(request.getProfileImage())
                .status(request.getStatus() != null ? request.getStatus() : Employee.EmployeeStatus.ACTIVE)
                .build();
    }

    public EmployeeDTO toDto(Employee entity) {
        if (entity == null) {
            return null;
        }

        return EmployeeDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .officialEmail(entity.getOfficialEmail())
                .personalEmail(entity.getPersonalEmail())
                .primaryMobile(entity.getPrimaryMobile())
                .alternateMobile(entity.getAlternateMobile())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .gender(entity.getGender())
                .dob(entity.getDob())
                .bloodGroup(entity.getBloodGroup())
                .departmentId(entity.getDepartmentId())
                .designationId(entity.getDesignationId())
                .managerId(entity.getManagerId())
                .joiningDate(entity.getJoiningDate())
                .employmentType(entity.getEmploymentType())
                .salary(entity.getSalary())
                .skills(entity.getSkills() != null ? new ArrayList<>(entity.getSkills()) : new ArrayList<>())
                .githubUsername(entity.getGithubUsername())
                .profileImage(entity.getProfileImage())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public EmployeeResponse toResponse(Employee entity) {
        if (entity == null) {
            return null;
        }

        String fullName = ((entity.getFirstName() != null ? entity.getFirstName() : "") + " " +
                           (entity.getLastName() != null ? entity.getLastName() : "")).trim();

        return EmployeeResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .officialEmail(entity.getOfficialEmail())
                .personalEmail(entity.getPersonalEmail())
                .primaryMobile(entity.getPrimaryMobile())
                .alternateMobile(entity.getAlternateMobile())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(fullName)
                .gender(entity.getGender())
                .dob(entity.getDob())
                .bloodGroup(entity.getBloodGroup())
                .departmentId(entity.getDepartmentId())
                .designationId(entity.getDesignationId())
                .managerId(entity.getManagerId())
                .joiningDate(entity.getJoiningDate())
                .employmentType(entity.getEmploymentType())
                .salary(entity.getSalary())
                .skills(entity.getSkills() != null ? new ArrayList<>(entity.getSkills()) : new ArrayList<>())
                .githubUsername(entity.getGithubUsername())
                .profileImage(entity.getProfileImage())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void updateEntityFromRequest(UpdateEmployeeRequest request, Employee entity) {
        if (request == null || entity == null) {
            return;
        }

        if (request.getPersonalEmail() != null) entity.setPersonalEmail(request.getPersonalEmail());
        if (request.getPrimaryMobile() != null) entity.setPrimaryMobile(request.getPrimaryMobile());
        if (request.getAlternateMobile() != null) entity.setAlternateMobile(request.getAlternateMobile());
        if (request.getFirstName() != null) entity.setFirstName(request.getFirstName());
        if (request.getLastName() != null) entity.setLastName(request.getLastName());
        if (request.getGender() != null) entity.setGender(request.getGender());
        if (request.getDob() != null) entity.setDob(request.getDob());
        if (request.getBloodGroup() != null) entity.setBloodGroup(request.getBloodGroup());
        if (request.getDepartmentId() != null) entity.setDepartmentId(request.getDepartmentId());
        if (request.getDesignationId() != null) entity.setDesignationId(request.getDesignationId());
        if (request.getManagerId() != null) entity.setManagerId(request.getManagerId());
        if (request.getJoiningDate() != null) entity.setJoiningDate(request.getJoiningDate());
        if (request.getEmploymentType() != null) entity.setEmploymentType(request.getEmploymentType());
        if (request.getSalary() != null) entity.setSalary(request.getSalary());
        if (request.getSkills() != null) entity.setSkills(new ArrayList<>(request.getSkills()));
        if (request.getGithubUsername() != null) entity.setGithubUsername(request.getGithubUsername());
        if (request.getProfileImage() != null) entity.setProfileImage(request.getProfileImage());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
    }
}
