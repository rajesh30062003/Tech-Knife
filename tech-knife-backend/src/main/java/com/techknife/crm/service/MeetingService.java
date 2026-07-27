package com.techknife.crm.service;

import com.techknife.crm.dto.MeetingDTO;
import com.techknife.crm.entity.Meeting;
import com.techknife.crm.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public List<MeetingDTO> getMeetingsByEntity(String entityType, String entityId) {
        return meetingRepository.findByEntityTypeAndEntityId(entityType.toUpperCase(), entityId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<MeetingDTO> getAllMeetings(String status) {
        List<Meeting> meetings;
        if (status != null && !status.isEmpty()) {
            meetings = meetingRepository.findByStatus(status.toUpperCase());
        } else {
            meetings = meetingRepository.findAll();
        }
        return meetings.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public MeetingDTO getMeetingById(String id) {
        Meeting m = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found with id: " + id));
        return mapToDTO(m);
    }

    public MeetingDTO createMeeting(MeetingDTO dto) {
        Meeting meeting = Meeting.builder()
                .title(dto.getTitle())
                .entityType(dto.getEntityType() != null ? dto.getEntityType().toUpperCase() : "LEAD")
                .entityId(dto.getEntityId())
                .agenda(dto.getAgenda())
                .participants(dto.getParticipants() != null ? dto.getParticipants() : List.of())
                .meetingTime(dto.getMeetingTime())
                .meetingNotes(dto.getMeetingNotes())
                .outcome(dto.getOutcome())
                .followUpTasks(dto.getFollowUpTasks() != null ? dto.getFollowUpTasks() : List.of())
                .status(dto.getStatus() != null ? dto.getStatus() : "SCHEDULED")
                .organizerId(dto.getOrganizerId())
                .build();

        Meeting saved = meetingRepository.save(meeting);
        log.info("Scheduled Meeting: {} for entity {}", saved.getTitle(), saved.getEntityId());
        return mapToDTO(saved);
    }

    public MeetingDTO updateMeeting(String id, MeetingDTO dto) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meeting not found with id: " + id));

        if (dto.getTitle() != null) meeting.setTitle(dto.getTitle());
        if (dto.getAgenda() != null) meeting.setAgenda(dto.getAgenda());
        if (dto.getParticipants() != null) meeting.setParticipants(dto.getParticipants());
        if (dto.getMeetingTime() != null) meeting.setMeetingTime(dto.getMeetingTime());
        if (dto.getMeetingNotes() != null) meeting.setMeetingNotes(dto.getMeetingNotes());
        if (dto.getOutcome() != null) meeting.setOutcome(dto.getOutcome());
        if (dto.getFollowUpTasks() != null) meeting.setFollowUpTasks(dto.getFollowUpTasks());
        if (dto.getStatus() != null) meeting.setStatus(dto.getStatus());

        Meeting updated = meetingRepository.save(meeting);
        return mapToDTO(updated);
    }

    public void deleteMeeting(String id) {
        if (!meetingRepository.existsById(id)) {
            throw new RuntimeException("Meeting not found with id: " + id);
        }
        meetingRepository.deleteById(id);
    }

    public MeetingDTO mapToDTO(Meeting m) {
        return MeetingDTO.builder()
                .id(m.getId())
                .title(m.getTitle())
                .entityType(m.getEntityType())
                .entityId(m.getEntityId())
                .agenda(m.getAgenda())
                .participants(m.getParticipants())
                .meetingTime(m.getMeetingTime())
                .meetingNotes(m.getMeetingNotes())
                .outcome(m.getOutcome())
                .followUpTasks(m.getFollowUpTasks())
                .status(m.getStatus())
                .organizerId(m.getOrganizerId())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}
