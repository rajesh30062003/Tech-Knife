package com.techknife.crm.service;

import com.techknife.crm.dto.CommunicationLogDTO;
import com.techknife.crm.entity.CommunicationLog;
import com.techknife.crm.repository.CommunicationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationLogService {

    private final CommunicationLogRepository communicationLogRepository;

    public List<CommunicationLogDTO> getLogsByEntity(String entityType, String entityId) {
        return communicationLogRepository.findByEntityTypeAndEntityId(entityType.toUpperCase(), entityId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public CommunicationLogDTO createLog(CommunicationLogDTO dto) {
        CommunicationLog logEntry = CommunicationLog.builder()
                .entityType(dto.getEntityType() != null ? dto.getEntityType().toUpperCase() : "LEAD")
                .entityId(dto.getEntityId())
                .type(dto.getType() != null ? dto.getType().toUpperCase() : "NOTE")
                .direction(dto.getDirection() != null ? dto.getDirection().toUpperCase() : "OUTBOUND")
                .subject(dto.getSubject())
                .content(dto.getContent())
                .conductedBy(dto.getConductedBy())
                .conductedByName(dto.getConductedByName())
                .timestamp(dto.getTimestamp() != null ? dto.getTimestamp() : Instant.now())
                .build();

        CommunicationLog saved = communicationLogRepository.save(logEntry);
        log.info("Logged Communication: {} for {} - {}", saved.getType(), saved.getEntityType(), saved.getEntityId());
        return mapToDTO(saved);
    }

    public void deleteLog(String id) {
        if (!communicationLogRepository.existsById(id)) {
            throw new RuntimeException("Communication Log not found with id: " + id);
        }
        communicationLogRepository.deleteById(id);
    }

    public CommunicationLogDTO mapToDTO(CommunicationLog log) {
        return CommunicationLogDTO.builder()
                .id(log.getId())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .type(log.getType())
                .direction(log.getDirection())
                .subject(log.getSubject())
                .content(log.getContent())
                .conductedBy(log.getConductedBy())
                .conductedByName(log.getConductedByName())
                .timestamp(log.getTimestamp())
                .createdAt(log.getCreatedAt())
                .updatedAt(log.getUpdatedAt())
                .build();
    }
}
