package com.techknife.communication.service;

import com.techknife.communication.dto.CalendarEventDTO;
import com.techknife.communication.dto.EventCalendarDTO;

import java.time.Instant;
import java.util.List;

public interface EventCalendarService {

    // Calendars
    EventCalendarDTO createCalendar(EventCalendarDTO dto);

    EventCalendarDTO updateCalendar(String id, EventCalendarDTO dto);

    EventCalendarDTO getCalendarById(String id);

    List<EventCalendarDTO> getUserCalendars(String userId);

    void deleteCalendar(String id);

    // Events
    CalendarEventDTO createEvent(CalendarEventDTO dto);

    CalendarEventDTO updateEvent(String id, CalendarEventDTO dto);

    CalendarEventDTO getEventById(String id);

    List<CalendarEventDTO> getEventsByCalendar(String calendarId);

    List<CalendarEventDTO> getEventsByDateRange(Instant start, Instant end);

    void deleteEvent(String id);
}
