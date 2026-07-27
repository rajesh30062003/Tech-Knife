package com.techknife.project.repository;

import com.techknife.project.entity.TaskComment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository extends MongoRepository<TaskComment, String> {

    List<TaskComment> findByTaskIdOrderByCreatedAtAsc(String taskId);
}
