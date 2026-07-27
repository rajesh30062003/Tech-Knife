package com.techknife.communication.repository;

import com.techknife.communication.entity.NotificationQueue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationQueueRepository extends MongoRepository<NotificationQueue, String> {
    List<NotificationQueue> findByStatus(String status);
}
