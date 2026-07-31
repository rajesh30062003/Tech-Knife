package com.techknife.project.repository;

import com.techknife.project.entity.ProjectAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectAssignmentRepository extends MongoRepository<ProjectAssignment, String> {
    List<ProjectAssignment> findByEmployeeId(String employeeId);
    List<ProjectAssignment> findByProjectId(String projectId);
    List<ProjectAssignment> findByEmployeeIdAndStatus(String employeeId, String status);
    List<ProjectAssignment> findByProjectIdAndStatus(String projectId, String status);
    void deleteByProjectId(String projectId);
}
