package com.techknife.project.sprint.repository;

import com.techknife.project.sprint.entity.SprintReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SprintReviewRepository extends MongoRepository<SprintReview, String> {

    Optional<SprintReview> findBySprintId(String sprintId);
}
