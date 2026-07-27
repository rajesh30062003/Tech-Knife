package com.techknife.crm.repository;

import com.techknife.crm.entity.FollowUp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowUpRepository extends MongoRepository<FollowUp, String> {
    List<FollowUp> findByEntityTypeAndEntityId(String entityType, String entityId);
    List<FollowUp> findByAssignedEmployeeId(String assignedEmployeeId);
    List<FollowUp> findByStatus(String status);
}
