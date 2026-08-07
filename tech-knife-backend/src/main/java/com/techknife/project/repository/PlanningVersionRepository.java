package com.techknife.project.repository;

import com.techknife.project.entity.PlanningVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanningVersionRepository extends MongoRepository<PlanningVersion, String> {
    List<PlanningVersion> findByProjectIdOrderByVersionNumberDesc(String projectId);
    List<PlanningVersion> findByDocumentIdOrderByVersionNumberDesc(String documentId);
}
