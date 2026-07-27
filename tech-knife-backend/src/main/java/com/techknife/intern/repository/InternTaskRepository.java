package com.techknife.intern.repository;

import com.techknife.intern.entity.InternTask;
import com.techknife.intern.entity.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternTaskRepository extends MongoRepository<InternTask, String> {
    List<InternTask> findByInternId(String internId);
    List<InternTask> findByMentorId(String mentorId);
    List<InternTask> findByInternIdAndStatus(String internId, TaskStatus status);
}
