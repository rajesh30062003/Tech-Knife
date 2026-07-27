package com.techknife.crm.repository;

import com.techknife.crm.entity.Meeting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends MongoRepository<Meeting, String> {
    List<Meeting> findByEntityTypeAndEntityId(String entityType, String entityId);
    List<Meeting> findByOrganizerId(String organizerId);
    List<Meeting> findByStatus(String status);
}
