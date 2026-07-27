package com.techknife.project.sprint.repository;

import com.techknife.project.sprint.entity.Sprint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SprintRepository extends MongoRepository<Sprint, String> {

    List<Sprint> findByProjectId(String projectId);

    Optional<Sprint> findByProjectIdAndStatus(String projectId, String status);

    List<Sprint> findByStatus(String status);
}
