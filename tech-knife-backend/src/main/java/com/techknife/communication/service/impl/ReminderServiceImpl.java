package com.techknife.communication.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.communication.dto.ReminderDTO;
import com.techknife.communication.entity.Reminder;
import com.techknife.communication.repository.ReminderRepository;
import com.techknife.communication.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;

    @Override
    public ReminderDTO createReminder(ReminderDTO dto) {
        Reminder reminder = Reminder.builder()
                .userId(dto.getUserId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .reminderTime(dto.getReminderTime())
                .status("PENDING")
                .priority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM")
                .entityType(dto.getEntityType())
                .entityId(dto.getEntityId())
                .isRecurring(dto.isRecurring())
                .recurrenceRule(dto.getRecurrenceRule())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return mapToDTO(reminderRepository.save(reminder));
    }

    @Override
    public ReminderDTO updateReminder(String id, ReminderDTO dto) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));

        reminder.setTitle(dto.getTitle());
        reminder.setDescription(dto.getDescription());
        reminder.setReminderTime(dto.getReminderTime());
        if (dto.getStatus() != null) reminder.setStatus(dto.getStatus());
        if (dto.getPriority() != null) reminder.setPriority(dto.getPriority());
        reminder.setEntityType(dto.getEntityType());
        reminder.setEntityId(dto.getEntityId());
        reminder.setRecurring(dto.isRecurring());
        reminder.setRecurrenceRule(dto.getRecurrenceRule());
        reminder.setUpdatedAt(Instant.now());

        return mapToDTO(reminderRepository.save(reminder));
    }

    @Override
    public ReminderDTO markStatus(String id, String status) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));
        reminder.setStatus(status);
        reminder.setUpdatedAt(Instant.now());
        return mapToDTO(reminderRepository.save(reminder));
    }

    @Override
    public ReminderDTO getReminderById(String id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));
        return mapToDTO(reminder);
    }

    @Override
    public List<ReminderDTO> getUserReminders(String userId, String status) {
        List<Reminder> list;
        if (status != null && !status.isBlank()) {
            list = reminderRepository.findByUserIdAndStatus(userId, status);
        } else {
            list = reminderRepository.findByUserIdOrderByReminderTimeAsc(userId);
        }
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteReminder(String id) {
        if (!reminderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reminder", "id", id);
        }
        reminderRepository.deleteById(id);
    }

    private ReminderDTO mapToDTO(Reminder r) {
        return ReminderDTO.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .title(r.getTitle())
                .description(r.getDescription())
                .reminderTime(r.getReminderTime())
                .status(r.getStatus())
                .priority(r.getPriority())
                .entityType(r.getEntityType())
                .entityId(r.getEntityId())
                .isRecurring(r.isRecurring())
                .recurrenceRule(r.getRecurrenceRule())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(r.getCreatedBy())
                .build();
    }
}
