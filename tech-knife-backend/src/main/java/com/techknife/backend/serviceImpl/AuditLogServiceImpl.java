package com.techknife.backend.serviceImpl;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.entity.AuditLog;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.backend.repository.AuditLogRepository;
import com.techknife.backend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    @org.springframework.beans.factory.annotation.Qualifier("backendAuditLogRepository")
    private final AuditLogRepository auditLogRepository;


    @Override
    public PagedResponse<AuditLog> getPaginatedAuditLogs(int page, int size, String principal, String module, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> auditPage;

        if (principal != null && !principal.trim().isEmpty()) {
            auditPage = auditLogRepository.findByPrincipal(principal.trim(), pageable);
        } else if (module != null && !module.trim().isEmpty()) {
            auditPage = auditLogRepository.findByModule(module.trim(), pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            auditPage = auditLogRepository.findByStatus(status.trim(), pageable);
        } else {
            auditPage = auditLogRepository.findAll(pageable);
        }

        return PagedResponse.<AuditLog>builder()
                .content(auditPage.getContent())
                .page(auditPage.getNumber())
                .size(auditPage.getSize())
                .totalElements(auditPage.getTotalElements())
                .totalPages(auditPage.getTotalPages())
                .last(auditPage.isLast())
                .build();
    }

    @Override
    public AuditLog getAuditLogById(String id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", "id", id));
    }

    @Override
    public void logAction(String action, String module, String entityType, String entityId, String performedBy, String details, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .module(module)
                .method(entityType + ":" + entityId)
                .principal(performedBy)
                .requestPayload(details)
                .ipAddress(ipAddress)
                .status("SUCCESS")
                .timestamp(java.time.Instant.now())
                .build();
        auditLogRepository.save(auditLog);
    }

}

