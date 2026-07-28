package com.techknife.backend.repository;

import com.techknife.backend.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("backendAuditLogRepository")
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {


    List<AuditLog> findByPrincipalOrderByCreatedAtDesc(String principal);

    List<AuditLog> findByModuleOrderByCreatedAtDesc(String module);

    Page<AuditLog> findByPrincipal(String principal, Pageable pageable);

    Page<AuditLog> findByModule(String module, Pageable pageable);

    Page<AuditLog> findByStatus(String status, Pageable pageable);
}
