package com.techknife.communication.repository;

import com.techknife.communication.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
    List<Notification> findByRecipientIdAndStatusOrderByCreatedAtDesc(String recipientId, String status);
    long countByRecipientIdAndStatus(String recipientId, String status);
}
