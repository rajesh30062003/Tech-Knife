package com.techknife.project.repository;

import com.techknife.project.entity.PlanningDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanningDocumentRepository extends MongoRepository<PlanningDocument, String> {
    Optional<PlanningDocument> findByProjectId(String projectId);
}
