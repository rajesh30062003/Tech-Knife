package com.techknife.communication.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.communication.dto.CalendarEventDTO;
import com.techknife.communication.dto.EventCalendarDTO;
import com.techknife.communication.entity.CalendarEvent;
import com.techknife.communication.entity.EventCalendar;
import com.techknife.communication.repository.CalendarEventRepository;
import com.techknife.communication.repository.EventCalendarRepository;
import com.techknife.communication.service.EventCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventCalendarServiceImpl implements EventCalendarService {

    private final EventCalendarRepository calendarRepository;
    private final CalendarEventRepository eventRepository;

    // Calendars
    @Override
    public EventCalendarDTO createCalendar(EventCalendarDTO dto) {
        EventCalendar calendar = EventCalendar.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .colorCode(dto.getColorCode())
                .ownerId(dto.getOwnerId())
                .isPublic(dto.isPublic())
                .sharedWithUserIds(dto.getSharedWithUserIds())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return mapToCalendarDTO(calendarRepository.save(calendar));
    }

    @Override
    public EventCalendarDTO updateCalendar(String id, EventCalendarDTO dto) {
        EventCalendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventCalendar", "id", id));

        calendar.setName(dto.getName());
        calendar.setDescription(dto.getDescription());
        calendar.setColorCode(dto.getColorCode());
        calendar.setPublic(dto.isPublic());
        calendar.setSharedWithUserIds(dto.getSharedWithUserIds());
        calendar.setUpdatedAt(Instant.now());

        return mapToCalendarDTO(calendarRepository.save(calendar));
    }

    @Override
    public EventCalendarDTO getCalendarById(String id) {
        EventCalendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventCalendar", "id", id));
        return mapToCalendarDTO(calendar);
    }

    @Override
    public List<EventCalendarDTO> getUserCalendars(String userId) {
        List<EventCalendar> owned = calendarRepository.findByOwnerId(userId);
        List<EventCalendar> publicCals = calendarRepository.findByIsPublicTrue();
        List<EventCalendar> shared = calendarRepository.findBySharedWithUserIdsContaining(userId);

        owned.addAll(publicCals);
        owned.addAll(shared);

        return owned.stream().distinct().map(this::mapToCalendarDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteCalendar(String id) {
        if (!calendarRepository.existsById(id)) {
            throw new ResourceNotFoundException("EventCalendar", "id", id);
        }
        calendarRepository.deleteById(id);
    }

    // Events
    @Override
    public CalendarEventDTO createEvent(CalendarEventDTO dto) {
        CalendarEvent event = CalendarEvent.builder()
                .calendarId(dto.getCalendarId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .isAllDay(dto.isAllDay())
                .eventType(dto.getEventType() != null ? dto.getEventType() : "GENERAL")
                .organizerId(dto.getOrganizerId())
                .attendees(dto.getAttendees())
                .status(dto.getStatus() != null ? dto.getStatus() : "CONFIRMED")
                .meetingLink(dto.getMeetingLink())
                .reminderMinutesBefore(dto.getReminderMinutesBefore())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return mapToEventDTO(eventRepository.save(event));
    }

    @Override
    public CalendarEventDTO updateEvent(String id, CalendarEventDTO dto) {
        CalendarEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CalendarEvent", "id", id));

        event.setCalendarId(dto.getCalendarId());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setAllDay(dto.isAllDay());
        if (dto.getEventType() != null) event.setEventType(dto.getEventType());
        if (dto.getAttendees() != null) event.setAttendees(dto.getAttendees());
        if (dto.getStatus() != null) event.setStatus(dto.getStatus());
        if (dto.getMeetingLink() != null) event.setMeetingLink(dto.getMeetingLink());
        if (dto.getReminderMinutesBefore() != null) event.setReminderMinutesBefore(dto.getReminderMinutesBefore());
        event.setUpdatedAt(Instant.now());

        return mapToEventDTO(eventRepository.save(event));
    }

    @Override
    public CalendarEventDTO getEventById(String id) {
        CalendarEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CalendarEvent", "id", id));
        return mapToEventDTO(event);
    }

    @Override
    public List<CalendarEventDTO> getEventsByCalendar(String calendarId) {
        return eventRepository.findByCalendarId(calendarId).stream()
                .map(this::mapToEventDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CalendarEventDTO> getEventsByDateRange(Instant start, Instant end) {
        return eventRepository.findByStartTimeBetween(start, end).stream()
                .map(this::mapToEventDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("CalendarEvent", "id", id);
        }
        eventRepository.deleteById(id);
    }

    private EventCalendarDTO mapToCalendarDTO(EventCalendar c) {
        return EventCalendarDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .colorCode(c.getColorCode())
                .ownerId(c.getOwnerId())
                .isPublic(c.isPublic())
                .sharedWithUserIds(c.getSharedWithUserIds())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(c.getCreatedBy())
                .build();
    }

    private CalendarEventDTO mapToEventDTO(CalendarEvent e) {
        return CalendarEventDTO.builder()
                .id(e.getId())
                .calendarId(e.getCalendarId())
                .title(e.getTitle())
                .description(e.getDescription())
                .location(e.getLocation())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .isAllDay(e.isAllDay())
                .eventType(e.getEventType())
                .organizerId(e.getOrganizerId())
                .attendees(e.getAttendees())
                .status(e.getStatus())
                .meetingLink(e.getMeetingLink())
                .reminderMinutesBefore(e.getReminderMinutesBefore())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .createdBy(e.getCreatedBy())
                .build();
    }
}
