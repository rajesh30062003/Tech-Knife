package com.techknife.backend.mapper;

import com.techknife.backend.dto.CreateEmployeeRequest;
import com.techknife.backend.dto.EmployeeDTO;
import com.techknife.backend.dto.EmployeeResponse;
import com.techknife.backend.dto.UpdateEmployeeRequest;
import com.techknife.backend.entity.Employee;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
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

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    public EmployeeResponse toResponse(Employee entity) {
        if (entity == null) {
            return null;
        }

        String fullName = ((entity.getFirstName() != null ? entity.getFirstName() : "") + " " +
                           (entity.getLastName() != null ? entity.getLastName() : "")).trim();

        List<Object> currentProjects = resolveCurrentProjects(entity);
        String managerName = resolveReportingManager(entity);

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
                .managerName(managerName)
                .currentProjects(currentProjects)
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

    private List<Object> resolveCurrentProjects(Employee entity) {
        if (entity == null || mongoTemplate == null) {
            return List.of();
        }

        String empId = entity.getEmployeeId();
        String docId = entity.getId();

        Map<String, Map<String, String>> projectMap = new LinkedHashMap<>();

        // 1. Query project_assignments collection
        try {
            org.springframework.data.mongodb.core.query.Query paQuery = new org.springframework.data.mongodb.core.query.Query();
            paQuery.addCriteria(new org.springframework.data.mongodb.core.query.Criteria().orOperator(
                org.springframework.data.mongodb.core.query.Criteria.where("employeeId").is(empId),
                org.springframework.data.mongodb.core.query.Criteria.where("employeeId").is(docId)
            ));
            List<com.techknife.project.entity.ProjectAssignment> assignments = mongoTemplate.find(paQuery, com.techknife.project.entity.ProjectAssignment.class);
            for (com.techknife.project.entity.ProjectAssignment pa : assignments) {
                if (pa.getProjectName() != null && !pa.getProjectName().isBlank()) {
                    String pId = pa.getProjectId() != null ? pa.getProjectId() : pa.getProjectName();
                    Map<String, String> pRef = new LinkedHashMap<>();
                    pRef.put("id", pId);
                    pRef.put("name", pa.getProjectName());
                    projectMap.put(pId, pRef);
                }
            }
        } catch (Exception ignored) {}

        // 2. Query projects collection directly
        try {
            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
            query.addCriteria(new org.springframework.data.mongodb.core.query.Criteria().orOperator(
                org.springframework.data.mongodb.core.query.Criteria.where("projectManagerId").is(empId),
                org.springframework.data.mongodb.core.query.Criteria.where("projectManagerId").is(docId),
                org.springframework.data.mongodb.core.query.Criteria.where("projectLeadId").is(empId),
                org.springframework.data.mongodb.core.query.Criteria.where("projectLeadId").is(docId),
                org.springframework.data.mongodb.core.query.Criteria.where("assignedEmployees").in(empId, docId),
                org.springframework.data.mongodb.core.query.Criteria.where("assignedInterns").in(empId, docId)
            ));
            List<com.techknife.project.entity.Project> projects = mongoTemplate.find(query, com.techknife.project.entity.Project.class);
            for (com.techknife.project.entity.Project p : projects) {
                String name = p.getProjectName() != null ? p.getProjectName() : p.getProjectCode();
                String pId = p.getProjectId() != null ? p.getProjectId() : (p.getId() != null ? p.getId() : name);
                if (name != null && !name.isBlank()) {
                    Map<String, String> pRef = new LinkedHashMap<>();
                    pRef.put("id", pId);
                    pRef.put("name", name);
                    projectMap.put(pId, pRef);
                }
            }
        } catch (Exception ignored) {}

        if (projectMap.isEmpty()) {
            return List.of();
        }

        return new ArrayList<>(projectMap.values());
    }

    private String resolveReportingManager(Employee entity) {
        if (entity == null || mongoTemplate == null) return "Not Assigned";

        String empId = entity.getEmployeeId();
        String docId = entity.getId();

        Set<String> managers = new LinkedHashSet<>();

        // Collect all project IDs assigned to this employee/intern
        Set<String> projectIds = new HashSet<>();
        try {
            org.springframework.data.mongodb.core.query.Query paQuery = new org.springframework.data.mongodb.core.query.Query();
            paQuery.addCriteria(new org.springframework.data.mongodb.core.query.Criteria().orOperator(
                org.springframework.data.mongodb.core.query.Criteria.where("employeeId").is(empId),
                org.springframework.data.mongodb.core.query.Criteria.where("employeeId").is(docId)
            ));
            List<com.techknife.project.entity.ProjectAssignment> assignments = mongoTemplate.find(paQuery, com.techknife.project.entity.ProjectAssignment.class);
            for (com.techknife.project.entity.ProjectAssignment pa : assignments) {
                if (pa.getProjectId() != null) {
                    projectIds.add(pa.getProjectId());
                }
            }
        } catch (Exception ignored) {}

        try {
            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
            query.addCriteria(new org.springframework.data.mongodb.core.query.Criteria().orOperator(
                org.springframework.data.mongodb.core.query.Criteria.where("projectManagerId").is(empId),
                org.springframework.data.mongodb.core.query.Criteria.where("projectManagerId").is(docId),
                org.springframework.data.mongodb.core.query.Criteria.where("projectLeadId").is(empId),
                org.springframework.data.mongodb.core.query.Criteria.where("projectLeadId").is(docId),
                org.springframework.data.mongodb.core.query.Criteria.where("assignedEmployees").in(empId, docId),
                org.springframework.data.mongodb.core.query.Criteria.where("assignedInterns").in(empId, docId)
            ));
            List<com.techknife.project.entity.Project> projects = mongoTemplate.find(query, com.techknife.project.entity.Project.class);
            for (com.techknife.project.entity.Project p : projects) {
                if (p.getId() != null) projectIds.add(p.getId());
                if (p.getProjectId() != null) projectIds.add(p.getProjectId());
            }

            if (!projectIds.isEmpty()) {
                org.springframework.data.mongodb.core.query.Query pDetailsQuery = new org.springframework.data.mongodb.core.query.Query();
                pDetailsQuery.addCriteria(new org.springframework.data.mongodb.core.query.Criteria().orOperator(
                    org.springframework.data.mongodb.core.query.Criteria.where("id").in(projectIds),
                    org.springframework.data.mongodb.core.query.Criteria.where("projectId").in(projectIds)
                ));
                List<com.techknife.project.entity.Project> assignedProjects = mongoTemplate.find(pDetailsQuery, com.techknife.project.entity.Project.class);
                for (com.techknife.project.entity.Project p : assignedProjects) {
                    String pmName = p.getProjectManagerName();
                    if (pmName != null && !pmName.isBlank() && !"Unassigned".equalsIgnoreCase(pmName) && !"Not Assigned".equalsIgnoreCase(pmName)) {
                        managers.add(pmName);
                    } else if (p.getProjectLeadName() != null && !p.getProjectLeadName().isBlank() && !"Unassigned".equalsIgnoreCase(p.getProjectLeadName()) && !"Not Assigned".equalsIgnoreCase(p.getProjectLeadName())) {
                        managers.add(p.getProjectLeadName());
                    } else {
                        managers.add("Not Assigned");
                    }
                }
            }
        } catch (Exception ignored) {}

        if (managers.isEmpty()) {
            return "Not Assigned";
        }

        return String.join(", ", managers);
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
