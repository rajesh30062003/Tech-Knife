package com.techknife.crm.service;

import com.techknife.crm.dto.FollowUpDTO;
import com.techknife.crm.entity.FollowUp;
import com.techknife.crm.repository.FollowUpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowUpService {

    private final FollowUpRepository followUpRepository;

    public List<FollowUpDTO> getFollowUpsByEntity(String entityType, String entityId) {
        return followUpRepository.findByEntityTypeAndEntityId(entityType.toUpperCase(), entityId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<FollowUpDTO> getAllFollowUps(String status) {
        List<FollowUp> followUps;
        if (status != null && !status.isEmpty()) {
            followUps = followUpRepository.findByStatus(status.toUpperCase());
        } else {
            followUps = followUpRepository.findAll();
        }
        return followUps.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public FollowUpDTO getFollowUpById(String id) {
        FollowUp f = followUpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Follow-up not found with id: " + id));
        return mapToDTO(f);
    }

    public FollowUpDTO createFollowUp(FollowUpDTO dto) {
        FollowUp followUp = FollowUp.builder()
                .entityType(dto.getEntityType() != null ? dto.getEntityType().toUpperCase() : "LEAD")
                .entityId(dto.getEntityId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .reminderDate(dto.getReminderDate())
                .priority(dto.getPriority() != null ? dto.getPriority().toUpperCase() : "MEDIUM")
                .status(dto.getStatus() != null ? dto.getStatus().toUpperCase() : "PENDING")
                .assignedEmployeeId(dto.getAssignedEmployeeId())
                .build();

        FollowUp saved = followUpRepository.save(followUp);
        log.info("Created Follow-Up: {} for entity {}", saved.getTitle(), saved.getEntityId());
        return mapToDTO(saved);
    }

    public FollowUpDTO updateFollowUp(String id, FollowUpDTO dto) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Follow-up not found with id: " + id));

        if (dto.getTitle() != null) followUp.setTitle(dto.getTitle());
        if (dto.getDescription() != null) followUp.setDescription(dto.getDescription());
        if (dto.getReminderDate() != null) followUp.setReminderDate(dto.getReminderDate());
        if (dto.getPriority() != null) followUp.setPriority(dto.getPriority());
        if (dto.getStatus() != null) followUp.setStatus(dto.getStatus());
        if (dto.getAssignedEmployeeId() != null) followUp.setAssignedEmployeeId(dto.getAssignedEmployeeId());

        FollowUp updated = followUpRepository.save(followUp);
        return mapToDTO(updated);
    }

    public void deleteFollowUp(String id) {
        if (!followUpRepository.existsById(id)) {
            throw new RuntimeException("Follow-up not found with id: " + id);
        }
        followUpRepository.deleteById(id);
    }

    public FollowUpDTO mapToDTO(FollowUp f) {
        return FollowUpDTO.builder()
                .id(f.getId())
                .entityType(f.getEntityType())
                .entityId(f.getEntityId())
                .title(f.getTitle())
                .description(f.getDescription())
                .reminderDate(f.getReminderDate())
                .priority(f.getPriority())
                .status(f.getStatus())
                .assignedEmployeeId(f.getAssignedEmployeeId())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .build();
    }
}
