package com.techknife.holiday.service;

import com.techknife.holiday.dto.HolidayCalendarDTO;

import java.util.List;

public interface HolidayCalendarService {
    HolidayCalendarDTO createCalendar(HolidayCalendarDTO dto);
    HolidayCalendarDTO updateCalendar(String id, HolidayCalendarDTO dto);
    HolidayCalendarDTO addHolidayToCalendar(String calendarId, String holidayId);
    HolidayCalendarDTO removeHolidayFromCalendar(String calendarId, String holidayId);
    HolidayCalendarDTO getCalendarById(String id);
    HolidayCalendarDTO getCalendarByYearAndBranch(Integer year, String branchId);
    List<HolidayCalendarDTO> getCalendarsByYear(Integer year);
    List<HolidayCalendarDTO> getAllCalendars();
    void deleteCalendar(String id);
}
