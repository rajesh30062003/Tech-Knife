package com.techknife.crm.repository;

import com.techknife.crm.entity.CommunicationLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunicationLogRepository extends MongoRepository<CommunicationLog, String> {
    List<CommunicationLog> findByEntityTypeAndEntityId(String entityType, String entityId);
    List<CommunicationLog> findByConductedBy(String conductedBy);
    List<CommunicationLog> findByType(String type);
}
