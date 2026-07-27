package com.techknife.project.repository;

import com.techknife.project.entity.ProjectStatusHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectStatusHistoryRepository extends MongoRepository<ProjectStatusHistory, String> {

    List<ProjectStatusHistory> findByProjectIdOrderByChangedAtDesc(String projectId);
}
