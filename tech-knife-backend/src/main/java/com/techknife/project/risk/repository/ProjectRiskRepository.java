package com.techknife.project.risk.repository;

import com.techknife.project.risk.entity.ProjectRisk;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRiskRepository extends MongoRepository<ProjectRisk, String> {

    List<ProjectRisk> findByProjectId(String projectId);

    List<ProjectRisk> findByProjectIdAndStatus(String projectId, String status);

    List<ProjectRisk> findByStatus(String status);
}
