package com.techknife.audit.repository;

import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditLog;
import com.techknife.audit.entity.AuditModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

/**
 * Spring Data MongoDB repository for {@link AuditLog} documents.
 */
@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    Page<AuditLog> findByUserId(String userId, Pageable pageable);

    Page<AuditLog> findByModule(AuditModule module, Pageable pageable);

    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);

    Page<AuditLog> findByEntityId(String entityId, Pageable pageable);

    Page<AuditLog> findByTimestampBetween(Instant start, Instant end, Pageable pageable);

    Page<AuditLog> findByUserIdAndModule(String userId, AuditModule module, Pageable pageable);
}
