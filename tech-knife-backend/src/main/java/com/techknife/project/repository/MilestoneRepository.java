package com.techknife.project.repository;

import com.techknife.project.entity.Milestone;
import com.techknife.project.entity.MilestoneStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends MongoRepository<Milestone, String> {

    List<Milestone> findByProjectId(String projectId);

    List<Milestone> findByProjectIdAndStatus(String projectId, MilestoneStatus status);
}
