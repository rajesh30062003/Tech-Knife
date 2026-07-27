package com.techknife.holiday.repository;

import com.techknife.holiday.entity.Holiday;
import com.techknife.holiday.entity.HolidayType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends MongoRepository<Holiday, String> {
    List<Holiday> findByYear(Integer year);
    List<Holiday> findByYearAndBranchId(Integer year, String branchId);
    List<Holiday> findByYearAndType(Integer year, HolidayType type);
    List<Holiday> findByYearAndRestrictedTrue(Integer year);
    List<Holiday> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Holiday> findByActiveTrue();
}
