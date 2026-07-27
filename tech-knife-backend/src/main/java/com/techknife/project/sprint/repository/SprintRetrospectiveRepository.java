package com.techknife.project.sprint.repository;

import com.techknife.project.sprint.entity.SprintRetrospective;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SprintRetrospectiveRepository extends MongoRepository<SprintRetrospective, String> {

    Optional<SprintRetrospective> findBySprintId(String sprintId);
}
