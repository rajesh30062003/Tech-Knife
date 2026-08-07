package com.techknife.project.repository;

import com.techknife.project.entity.TaskActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskActivityLogRepository extends MongoRepository<TaskActivityLog, String> {
    List<TaskActivityLog> findByTaskIdOrderByTimestampDesc(String taskId);
    List<TaskActivityLog> findByProjectIdOrderByTimestampDesc(String projectId);
}
