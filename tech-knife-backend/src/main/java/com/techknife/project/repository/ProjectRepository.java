package com.techknife.project.repository;

import com.techknife.project.entity.Project;
import com.techknife.project.entity.ProjectStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    Optional<Project> findByProjectCode(String projectCode);

    boolean existsByProjectCode(String projectCode);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByProjectManagerId(String projectManagerId);

    List<Project> findByMembersEmployeeId(String employeeId);
}
