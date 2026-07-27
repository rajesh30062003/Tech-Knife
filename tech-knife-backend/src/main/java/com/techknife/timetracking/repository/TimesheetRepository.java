package com.techknife.timetracking.repository;

import com.techknife.timetracking.entity.Timesheet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimesheetRepository extends MongoRepository<Timesheet, String> {

    List<Timesheet> findByEmployeeId(String employeeId);

    List<Timesheet> findByStatus(String status);

    List<Timesheet> findByEmployeeIdAndStatus(String employeeId, String status);

    Optional<Timesheet> findByEmployeeIdAndPeriodStartDateAndPeriodEndDate(String employeeId, LocalDate startDate, LocalDate endDate);
}
