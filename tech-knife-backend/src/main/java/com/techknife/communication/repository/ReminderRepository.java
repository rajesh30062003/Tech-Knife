package com.techknife.communication.repository;

import com.techknife.communication.entity.Reminder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReminderRepository extends MongoRepository<Reminder, String> {
    List<Reminder> findByUserIdOrderByReminderTimeAsc(String userId);
    List<Reminder> findByUserIdAndStatus(String userId, String status);
    List<Reminder> findByReminderTimeBeforeAndStatus(Instant now, String status);
}
