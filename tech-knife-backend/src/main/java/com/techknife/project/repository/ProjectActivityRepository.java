package com.techknife.project.repository;

import com.techknife.project.entity.ProjectActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectActivityRepository extends MongoRepository<ProjectActivity, String> {
    List<ProjectActivity> findByProjectIdOrderByTimestampDesc(String projectId);
    List<ProjectActivity> findByProjectCodeOrderByTimestampDesc(String projectCode);
    List<ProjectActivity> findByProjectIdOrProjectCodeOrderByTimestampDesc(String projectId, String projectCode);
}
