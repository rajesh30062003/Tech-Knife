package com.techknife.project.repository;

import com.techknife.project.entity.TaskTimeLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskTimeLogRepository extends MongoRepository<TaskTimeLog, String> {
    List<TaskTimeLog> findByTaskIdOrderByLogDateDesc(String taskId);
    List<TaskTimeLog> findByProjectIdOrderByLogDateDesc(String projectId);
}
