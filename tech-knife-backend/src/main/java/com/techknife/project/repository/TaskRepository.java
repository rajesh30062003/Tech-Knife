package com.techknife.project.repository;

import com.techknife.project.entity.Task;
import com.techknife.project.entity.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    Optional<Task> findByTaskNumber(String taskNumber);

    boolean existsByTaskNumber(String taskNumber);

    List<Task> findByProjectId(String projectId);

    List<Task> findByProjectIdAndStatus(String projectId, TaskStatus status);

    List<Task> findByAssignedEmployeeId(String employeeId);

    List<Task> findByMilestoneId(String milestoneId);

    List<Task> findByParentTaskId(String parentTaskId);

    long countByProjectId(String projectId);
}
