package com.techknife.holiday.repository;

import com.techknife.holiday.entity.HolidayCalendar;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayCalendarRepository extends MongoRepository<HolidayCalendar, String> {
    Optional<HolidayCalendar> findByYearAndBranchId(Integer year, String branchId);
    List<HolidayCalendar> findByYear(Integer year);
    List<HolidayCalendar> findByActiveTrue();
}
