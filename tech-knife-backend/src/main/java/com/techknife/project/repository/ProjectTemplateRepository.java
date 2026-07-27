package com.techknife.project.repository;

import com.techknife.project.entity.ProjectTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectTemplateRepository extends MongoRepository<ProjectTemplate, String> {

    Optional<ProjectTemplate> findByTemplateCode(String templateCode);

    boolean existsByTemplateCode(String templateCode);
}
