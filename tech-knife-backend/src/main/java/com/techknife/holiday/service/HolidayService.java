package com.techknife.holiday.service;

import com.techknife.holiday.dto.HolidayDTO;
import com.techknife.holiday.entity.HolidayType;

import java.time.LocalDate;
import java.util.List;

public interface HolidayService {
    HolidayDTO createHoliday(HolidayDTO dto);
    HolidayDTO updateHoliday(String id, HolidayDTO dto);
    HolidayDTO getHolidayById(String id);
    List<HolidayDTO> getHolidaysByYear(Integer year);
    List<HolidayDTO> getHolidaysByYearAndBranch(Integer year, String branchId);
    List<HolidayDTO> getHolidaysByYearAndType(Integer year, HolidayType type);
    List<HolidayDTO> getRestrictedHolidays(Integer year);
    List<HolidayDTO> getHolidaysInRange(LocalDate startDate, LocalDate endDate);
    List<HolidayDTO> getAllHolidays();
    void deleteHoliday(String id);
}
