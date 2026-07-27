package com.techknife.timetracking.repository;

import com.techknife.timetracking.entity.TimeEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeEntryRepository extends MongoRepository<TimeEntry, String> {

    List<TimeEntry> findByEmployeeId(String employeeId);

    List<TimeEntry> findByProjectId(String projectId);

    List<TimeEntry> findByTaskId(String taskId);

    Optional<TimeEntry> findByEmployeeIdAndTimerRunningTrue(String employeeId);

    List<TimeEntry> findByEmployeeIdAndStartTimeBetween(String employeeId, Instant start, Instant end);

    List<TimeEntry> findByProjectIdAndStartTimeBetween(String projectId, Instant start, Instant end);
}
