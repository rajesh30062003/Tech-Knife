package com.techknife.communication.repository;

import com.techknife.communication.entity.CalendarEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CalendarEventRepository extends MongoRepository<CalendarEvent, String> {
    List<CalendarEvent> findByCalendarId(String calendarId);
    List<CalendarEvent> findByStartTimeBetween(Instant start, Instant end);
    List<CalendarEvent> findByAttendeesContaining(String userId);
    List<CalendarEvent> findByOrganizerId(String organizerId);
}
