package com.techknife.project.repository;

import com.techknife.project.entity.TaskAttachment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttachmentRepository extends MongoRepository<TaskAttachment, String> {

    List<TaskAttachment> findByTaskId(String taskId);

    List<TaskAttachment> findByProjectId(String projectId);
}
