package com.techknife.communication.repository;

import com.techknife.communication.entity.EventCalendar;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventCalendarRepository extends MongoRepository<EventCalendar, String> {
    List<EventCalendar> findByOwnerId(String ownerId);
    List<EventCalendar> findByIsPublicTrue();
    List<EventCalendar> findBySharedWithUserIdsContaining(String userId);
}
